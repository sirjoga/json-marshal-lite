package ru.fatrat.jsonmarshal;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import java.io.IOException;
import java.io.Reader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;

import static ru.fatrat.jsonmarshal.JSConfigReader.Ctx.*;

/**
 * JSON-like config support (multi-line strings, object field names without quote marks).
 */
public class JSConfigReader {
    
    enum Ctx {END_ARR, END_OBJ, ARR_WAIT_VAL, ARR_WAIT_COMMA, OBJ_WAIT_NAME, OBJ_WAIT_COLON, OBJ_WAIT_VAL, OBJ_WAIT_COMMA, OBJ_NAME, OBJ_QUOTEDNAME, VAL_STR, VAL_RAW_WORD, 
        ONELNCOMMENT, MULTILNCOMMENT_WAITASTERISK, MULTILNCOMMENT_WAITCLOSE, MULTILNCOMMENT,MLS_WAIT_SECOND_LT, MLS_WAIT_BOUNDARY_START, MLS_BOUNDARY, MLS_WS_AFTER_BOUNDARY, MLS_BODY
    }
    
    private static class State {
        Ctx ctx; 
        JsonStructure res; 
        String name;
        Object builder;
        Set<String> objHandledNames;
        JsonArrayBuilder aBuilder() { return (JsonArrayBuilder) builder; } 
        JsonObjectBuilder oBuilder() { return (JsonObjectBuilder) builder; } 
        State (Ctx ctx) {
            this.ctx = ctx;
            if (ctx == ARR_WAIT_VAL) builder = Json.createArrayBuilder();
            if (ctx == OBJ_WAIT_NAME) {
                builder = Json.createObjectBuilder();
                objHandledNames = new HashSet<>();
            }
        }
    }

    private interface R {
        JsonStructure read(Reader src);
    }

    /**
     * Read Json-like config.
     */
    public static JsonStructure read(Reader src) {
        R r = new R() {
            char ch;
            char qChar;
            int nRow; int nCol;
            final Stack<State> stack = new Stack<>();
            StringBuilder currVal = new StringBuilder();
            
            private JsonException err(String message, Throwable cause) {
                String s = String.format("Parse error at [%d,%d]", nRow, nCol);
                if (message != null) s += String.format("(%s)", message); 
                return new JsonException(s, cause);
            }
            
            private JsonException err(String message) { return err(message, null); }
            
    
            private boolean checkComment() {
                if (ch == '#') {
                    stack.push(new State(ONELNCOMMENT));
                    return true;
                } 
                if (ch == '/') {
                    stack.push(new State(MULTILNCOMMENT_WAITASTERISK));
                    return true;
                }
                return false;
            }
            
            private boolean isWs() {
                return " \n\r\t".indexOf(ch) >= 0;
            }
            
            private boolean checkWsOrComment() {
                if (isWs()) return true;
                if (checkComment()) return true;
                return false;
            }
            
            private boolean checkStructStart() {
                if (ch == '[') {
                    stack.push(new State(ARR_WAIT_VAL));
                    return true;
                } else if (ch == '{') {
                    stack.push(new State(OBJ_WAIT_NAME));
                    return true;
                }
                return false;
            }
            
            private boolean checkValueStart() {
                if (checkStructStart()) return true;
                if (String.valueOf(ch).matches("[A-Za-z_0-9\\-.+]")) {
                    stack.push(new State(VAL_RAW_WORD));
                    currVal = new StringBuilder();
                    currVal.append(ch);
                    return true;
                }
                if ("\"'`".indexOf(ch) >= 0) {
                    qChar = ch;
                    stack.push(new State(VAL_STR));
                    currVal = new StringBuilder();
                    return true;
                }
                if (ch == '<') {
                    stack.push(new State(MLS_WAIT_SECOND_LT));
                    return true;
                }
                return false;
            }
            
            private void handleResult(Object v) {
                if (stack.size() == 0) {
                    JsonStructure sV = (JsonStructure) v;
                    if (sV.getValueType() == ValueType.ARRAY) {
                        State st = new State(END_ARR);
                        st.res = sV;
                        stack.push(st);
                        return;
                    }
                    if (sV.getValueType() == ValueType.OBJECT) {
                        State st = new State(END_OBJ);
                        st.res = sV;
                        stack.push(st);
                        return;
                    }
                    throw err("Invalid root element");
                }
                State curr =  stack.peek();
                switch(curr.ctx) {
                case ARR_WAIT_VAL:
                    if (v == null) curr.aBuilder().addNull();
                    else if (v instanceof Long) curr.aBuilder().add((Long)v);
                    else if (v instanceof Double) curr.aBuilder().add((Double)v);
                    else if (v instanceof String) curr.aBuilder().add((String)v);
                    else if (v instanceof Boolean) curr.aBuilder().add((Boolean)v);
                    else if (v instanceof JsonValue) curr.aBuilder().add((JsonValue)v);
                    else throw err(String.format("Unknown value class: '%s'", v.getClass().getName()));
                    curr.ctx = ARR_WAIT_COMMA;
                    return;
                case OBJ_WAIT_VAL:
                    if (v == null) curr.oBuilder().addNull(curr.name);
                    else if (v instanceof Long) curr.oBuilder().add(curr.name, (Long)v);
                    else if (v instanceof Double) curr.oBuilder().add(curr.name, (Double)v);
                    else if (v instanceof String) curr.oBuilder().add(curr.name, (String)v);
                    else if (v instanceof Boolean) curr.oBuilder().add(curr.name, (Boolean)v);
                    else if (v instanceof JsonValue) curr.oBuilder().add(curr.name, (JsonValue)v);
                    else throw err(String.format("Unknown value class: '%s'", v.getClass().getName()));
                    curr.ctx = OBJ_WAIT_COMMA;
                    return;
                default:
                    throw err("invalid value destination");
                }
            }
            
            final List<String> mls = new ArrayList<>();

            void checkDupObjItem(State curr) {
                if (curr.objHandledNames.contains(curr.name)) 
                    throw err(String.format("duplicate object key: '%s'", curr.name));
                curr.objHandledNames.add(curr.name);
            }
            
            @Override public JsonStructure read(Reader src) {
                try {
                    int MLS_START = -10000;
                    int MLS_INVALID = -20000;
                    int mlsState = MLS_START; 
                    int newRow = 1; int newCol = 1;
                    NEXTCHAR: while(true) {
                        nRow = newRow; nCol = newCol;
                        int iCh = src.read();
                        if (iCh == -1 ) {
                            if (stack.size() != 1) throw err("Unexpected end of stream");
                            State curr = stack.pop();
                            if (curr.ctx == END_ARR) {
                                return curr.res;
                            }
                            if (curr.ctx == END_OBJ) {
                                return curr.res;
                            }
                            throw err("Unexpected end of stream");
                        }
                        ch = (char) iCh;
                        if (ch == '\n') { newRow++; newCol = 1; }
                        else if (ch != '\t') newCol++;
                        SAMECHAR: while (true) {
                            
                            if (stack.size() == 0) { // initial
                                if (checkWsOrComment()) continue NEXTCHAR;
                                if (checkStructStart()) continue NEXTCHAR;
                                throw err("Only array/object allowed at root level");
                            }
                            State curr = stack.peek(); 
                            switch (curr.ctx) {
                            case END_ARR:
                            case END_OBJ:
                                if (checkWsOrComment()) continue NEXTCHAR;
                                throw err("Garbage at the end of stream");
                            case ONELNCOMMENT:
                                if (ch == '\n') stack.pop();
                                continue NEXTCHAR;
                            case MULTILNCOMMENT_WAITASTERISK:
                                if (ch == '/') {
                                    curr.ctx = ONELNCOMMENT;
                                    continue NEXTCHAR;
                                }
                                if (ch!='*') throw err("'*' expected");
                                curr.ctx = MULTILNCOMMENT;
                                continue NEXTCHAR;
                            case MULTILNCOMMENT:
                                if (ch=='*') curr.ctx = MULTILNCOMMENT_WAITCLOSE;
                                continue NEXTCHAR;
                            case MULTILNCOMMENT_WAITCLOSE:
                                if (ch=='/') {
                                    stack.pop();
                                    continue NEXTCHAR;
                                }
                                if (ch=='*') continue NEXTCHAR;
                                curr.ctx = MULTILNCOMMENT;
                                continue NEXTCHAR;
                            case OBJ_WAIT_NAME:
                            case OBJ_WAIT_COMMA:
                                if (checkWsOrComment()) continue NEXTCHAR;
                                if ((curr.ctx == OBJ_WAIT_COMMA) && (ch == ',')) {
                                    curr.ctx = OBJ_WAIT_NAME;
                                    continue NEXTCHAR;
                                }
                                if (curr.ctx == OBJ_WAIT_NAME && "\"'`".indexOf(ch) >= 0) {
                                    qChar = ch;
                                    curr.ctx = OBJ_QUOTEDNAME;
                                    currVal = new StringBuilder();
                                    continue NEXTCHAR;
                                }
                                if (curr.ctx == OBJ_WAIT_NAME && String.valueOf(ch).matches("[A-Za-z_А-Яа-я]")) {
                                    curr.ctx = OBJ_NAME;
                                    currVal = new StringBuilder();
                                    currVal.append(ch);
                                    continue NEXTCHAR;
                                }
                                if (ch == '}') {
                                    stack.pop();
                                    handleResult(curr.oBuilder().build());
                                    continue NEXTCHAR;
                                }
                                throw err("Unclosed object");
                            case OBJ_NAME: 
                                if (String.valueOf(ch).matches("[A-Za-z_А-Яа-я0-9\\-]")) {
                                    currVal.append(ch);
                                    continue NEXTCHAR;
                                }
                                if (ch == ':') {
                                    curr.name = currVal.toString();
                                    checkDupObjItem(curr);
                                    curr.ctx = OBJ_WAIT_VAL;
                                    continue NEXTCHAR;
                                }
                                curr.name = currVal.toString();
                                curr.ctx = OBJ_WAIT_COLON;
                                continue SAMECHAR;
                            case OBJ_QUOTEDNAME:
                                if (ch == qChar) {
                                    curr.name = currVal.toString();
                                    curr.ctx = OBJ_WAIT_COLON;
                                    continue NEXTCHAR;
                                }
                                currVal.append(ch);
                                continue NEXTCHAR;
                            case OBJ_WAIT_COLON:
                                if (checkWsOrComment()) continue NEXTCHAR;
                                if (ch!=':') throw err("Colon expected");
                                checkDupObjItem(curr);
                                curr.ctx = Ctx.OBJ_WAIT_VAL;
                                continue NEXTCHAR;
                            case OBJ_WAIT_VAL: 
                                if (checkWsOrComment()) continue NEXTCHAR;
                                if (checkValueStart()) continue NEXTCHAR;
                                throw err("Value expected");
                            case ARR_WAIT_COMMA:
                            case ARR_WAIT_VAL:
                                if (checkWsOrComment()) continue NEXTCHAR;
                                if ((curr.ctx == ARR_WAIT_COMMA) && (ch == ',')) {
                                    curr.ctx = ARR_WAIT_VAL;
                                    continue NEXTCHAR;
                                }
                                if (curr.ctx == ARR_WAIT_VAL && checkValueStart()) continue NEXTCHAR;
                                if (ch == ']') {
                                    stack.pop();
                                    handleResult(curr.aBuilder().build());
                                    continue NEXTCHAR;
                                }
                                throw err("Unclosed array");
                            case VAL_STR: 
                                if (ch == '\r') continue NEXTCHAR;
                                if (ch == qChar) {
                                    stack.pop();
                                    handleResult(currVal.toString());
                                    continue NEXTCHAR;
                                }
                                currVal.append(ch);
                                continue NEXTCHAR;
                            case VAL_RAW_WORD: 
                                if (String.valueOf(ch).matches("[A-Za-z_0-9\\-.+]")) {
                                    currVal.append(ch);
                                    continue NEXTCHAR;
                                }
                                {
                                    stack.pop();
                                    String s = currVal.toString();
                                    if (s.equals("true")) handleResult(true);
                                    else if (s.equals("false")) handleResult(false);
                                    else if (s.equals("null")) handleResult(null);
                                    else if (s.matches("[\\-+0-9]+")) handleResult(Long.parseLong(s));
                                    else try {
                                        handleResult(NumberFormat.getInstance(Locale.ROOT).parse(s).doubleValue());
                                    } catch (ParseException e) {
                                        throw err(String.format("Value parse error: '%s'", s));
                                    }
                                }
                                continue SAMECHAR;
                            case MLS_WAIT_SECOND_LT:
                                if (ch != '<') throw err("Second '<' expected");
                                curr.ctx = MLS_WAIT_BOUNDARY_START;
                                continue NEXTCHAR;
                            case MLS_WAIT_BOUNDARY_START:
                                if (ch == '\n') throw err("Boundary expected");
                                if (isWs()) continue NEXTCHAR;
                                curr.ctx = MLS_BOUNDARY;
                                currVal = new StringBuilder();
                                currVal.append(ch);
                                continue NEXTCHAR;
                            case MLS_BOUNDARY:
                                if (isWs()) {
                                    curr.name = currVal.toString();
                                    curr.ctx = MLS_WS_AFTER_BOUNDARY;
                                    continue SAMECHAR;
                                }
                                currVal.append(ch);
                                continue NEXTCHAR;
                            case MLS_WS_AFTER_BOUNDARY:
                                if (ch == '\n') {
                                    curr.ctx = MLS_BODY;
                                    mls.clear();
                                    mlsState = MLS_START;
                                    currVal = new StringBuilder();
                                    continue NEXTCHAR;
                                }
                                if (isWs()) continue NEXTCHAR;
                                throw err("Garbage after boundary");
                            
                            case MLS_BODY:

                                  MLS: while (true){
                                     if (mlsState == MLS_INVALID) break MLS; 
                                    if (mlsState == MLS_START) {
                                        if (isWs()) mlsState = 1; else mlsState = 0;
                                        break MLS;
                                    }
                                    int cnl = curr.name.length();  
                                    if ((mlsState <= 0) && (isWs() || ch == ',') && currVal.length() + mlsState == cnl 
                                            && currVal.substring(-mlsState, -mlsState + cnl).equals(curr.name)) { // boundary detected
                                        mlsState = -mlsState;
                                        stack.pop();
                                        String indent = currVal.substring(0, mlsState); 
                                        currVal = new StringBuilder();
                                        boolean started = false;
                                        for (String s: mls) {
                                            if (mlsState > 0 && s.length() >= mlsState && s.substring(0, mlsState).equals(indent))
                                                s = s.substring(mlsState);
                                            if (started) currVal.append('\n');
                                            currVal.append(s);
                                            started = true;
                                        }
                                        mls.clear();
                                        handleResult(currVal.toString());
                                        currVal = new StringBuilder();
                                        continue SAMECHAR; 
                                    } 
                                    if (mlsState > 0) {
                                        if (isWs())
                                            mlsState ++;
                                        else 
                                            mlsState = -mlsState;
                                    } else if (isWs()) {
                                        mlsState = MLS_INVALID;
                                    }
                                    
                                    break MLS;
                                }
                                if (ch == '\r') continue NEXTCHAR;
                                if (ch == '\n') {
                                    mls.add(currVal.toString());
                                    currVal = new StringBuilder();
                                    mlsState = MLS_START;
                                    continue NEXTCHAR;
                                }
                                currVal.append(ch);
                                // continue NEXTCHAR; // uncomment causes strange runtime error when compiled in eclipse O_o 
                            } 
                            break SAMECHAR;
                        } 
                    } 
                } catch (IOException e) {
                    throw err("I/O error", e);
                }
            }
        };
        return r.read(src);
    } 
}
