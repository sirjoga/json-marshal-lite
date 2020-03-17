package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

/**
 * JsonGenerator helpers: 
 * <p><ul>
 *     <li>Split write* methods to setName and write parts
 *     <li>Write Buffering
 * </ul>
 *    
 * 
 */
public class JsonGeneratorHelper {
	
	private JsonGenerator generator;
	
	private enum Type { STRING, BOOLEAN, INT, FLOAT, STARTOBJ, STARTARR, END, NULL, VALUE }  
	private static class BufferRec {
		final Type type;
		final String name;
		final Object value;
		BufferRec(Type type, String name, Object value) {
			this.type = type; this.name = name; this.value = value;
		}
	}
	
	private static class StartRec {
		final String name;
		final int bufStart;
		final int level;
		StartRec(String name, int bufStart, int level) { this.name = name; this.bufStart = bufStart; this.level = level; }
	}
	
	private int level = 0;
	
	public int getLevel() { return level; }
	
	public void checkLevel(int level) {
		if (level != this.level) throw new JsonGeneratorHelperException("Ошибка баланса start/end");
	}
	
	
	private final LinkedList<BufferRec> buffer = new LinkedList<>();
	private final Stack<StartRec> starts = new Stack<>();
	

	public void startBuffer() {
		starts.push(new StartRec(name, buffer.size(), level));
	}
	
	public void endBuffer(boolean commit) {
		if (starts.size() == 0) throw new JsonGeneratorHelperException("Попытка вызова endBuffer до старта буферизации");
		StartRec start = starts.pop();
		
		if (!commit) {
			if (starts.size() == 0) {
				buffer.clear();
			} else {
				if (start.bufStart < buffer.size()) {
					Iterator<BufferRec> it = buffer.listIterator(start.bufStart);
					while (it.hasNext()) {
						it.next();
						it.remove();
					}
				}
			}
			name = start.name;
			level = start.level;
		} else {
			if (starts.size() == 0) {
				for (BufferRec br: buffer) {
					switch(br.type) {
					case STRING: writeString(br.name, (String) br.value); break;
					case FLOAT: writeFloat(br.name, (Double) br.value); break;
					case INT: writeInt(br.name, (Long) br.value); break;
					case BOOLEAN: writeBoolean(br.name, (Boolean) br.value); break;
					case VALUE: writeValue(br.name, (JsonValue) br.value); break;
					case END: writeEnd(); break;
					case STARTARR: writeStartArray(br.name); break;
					case STARTOBJ: writeStartObject(br.name); break;
					case NULL: writeNull(br.name); break;
					}
				}
				buffer.clear();
			}
		}
	}
	

	public JsonGeneratorHelper(JsonGenerator generator) {
		this.generator = generator;
	}

	private void writeStartObject(String n) {
		if (n != null) generator.writeStartObject(n); else generator.writeStartObject();
	}
	
	private void writeStartArray(String n) {
		if (n != null) generator.writeStartArray(n); else generator.writeStartArray();
	}
	
	private void writeString(String n, String value) {
		if (n!=null) generator.write(n, value); else generator.write(value);
	}
	
	private void writeFloat(String n, double value) {
		if (n!=null) generator.write(n, value); else generator.write(value);
	}
	
	private void writeInt(String n, long value) {
		if (n!=null) generator.write(n, value); else generator.write(value);
	}
	
	private void writeBoolean(String n, boolean value) {
		if (n!=null) generator.write(n, value); else generator.write(value);
	}
	
	private void writeValue(String n, JsonValue value) {
		if (n!=null) generator.write(n, value); else generator.write(value);
	}
	
	private void writeNull(String n) {
		if (n!=null) generator.writeNull(n); else generator.writeNull();
	}

	private String name = null;
	
	public void setName(@Nonnull String name) {
		this.name = name; 
	}

	public void cancelName() {
		if (name == null) throw new JsonGeneratorHelperException("Cannot cancel non-started name");
	}
	
	public void writeString(@Nonnull String value) {
		if (starts.size() > 0) 
			buffer.add(new BufferRec(Type.STRING, name, value));
		else 
			writeString(name, value); 
		name = null;
	}
	
	public void writeFloat(double value) {
		if (starts.size() > 0) 
			buffer.add(new BufferRec(Type.FLOAT, name, value));
		else
			writeFloat(name, value); 
		name = null;
	}
	
	public void writeInt(long value) {
		if (starts.size() > 0)
			buffer.add(new BufferRec(Type.INT, name, value));
		else
			writeInt(name, value); 
		name = null;
	}
	
	public void writeStartObject() {
		if (starts.size() > 0)
			buffer.add(new BufferRec(Type.STARTOBJ, name, null));
		else
			writeStartObject(name); 
		name = null;
		level++;
	}
	
	public void writeStartArray() {
		if (starts.size() > 0) 
			buffer.add(new BufferRec(Type.STARTARR, name, null));
		else
			writeStartArray(name); 
		name = null;
		level++;
	}
	
	public void writeValue(@Nonnull JsonValue value) {
		if (starts.size() > 0) 
			buffer.add(new BufferRec(Type.VALUE, name, value));
		else
			writeValue(name, value); 
		name = null;
	}
	

	public void writeBoolean(boolean value) {
		if (starts.size() > 0) 
			buffer.add(new BufferRec(Type.BOOLEAN, name, value));
		else
			writeBoolean(name, value); 
		name = null;
	}
	
	public void writeNull() {
		if (starts.size() > 0) 
			buffer.add(new BufferRec(Type.NULL, name, null));
		else
			writeNull(name); 
		name = null;
	}
	
	public void writeEnd() {
		if (name != null) throw new JsonGeneratorHelperException("writeEnd после setName не допускается");
		if (starts.size() > 0) 
			buffer.add(new BufferRec(Type.END, null, null));
		else
			generator.writeEnd();
		level--;
	}
	
	public JsonGenerator getGenerator() { return generator; }
}
