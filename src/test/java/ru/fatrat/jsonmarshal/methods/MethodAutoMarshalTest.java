package ru.fatrat.jsonmarshal.methods;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.fatrat.jsonmarshal.JsonGeneratorHelper;
import ru.fatrat.jsonmarshal.JsonMarshal;
import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonUnmarshal;

import javax.json.JsonValue;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MethodAutoMarshalTest {

    @Mock
    private TestObj testObj;

    @Mock
    private JsonMarshal marshal;

    @Mock
    private JsonUnmarshal unmarshal;

    @InjectMocks
    private MethodAutoMarshal subj;

    public static abstract class TestObj {
        public abstract void noArgNoRes();
        public abstract int noArgIntRes();
        public abstract void intArgNoRes(int a);
    }

    private static Method findMethod(String name) {
        return Arrays.stream(TestObj.class.getMethods()).filter(m -> m.getName().equals(name)).findAny().orElseThrow(() ->
                new IllegalArgumentException(String.format("No method: %s", name)));
    }

    @Test
    public void testNoArgRes() {
        AutoMarshalInstance instance = subj.create(testObj, findMethod("noArgNoRes"));
        assertFalse(instance.hasResult());
        assertFalse(instance.hasArgument());
        ResultMarshaller res = instance.process(null);
        verify(testObj, times(1)).noArgNoRes();
        verifyNoMoreInteractions(testObj);
    }

    @Test
    public void testNoArgIntRes() {
        AutoMarshalInstance instance = subj.create(testObj, findMethod("noArgIntRes"));
        assertTrue(instance.hasResult());
        assertFalse(instance.hasArgument());
        when(testObj.noArgIntRes()).thenReturn(1);
        ResultMarshaller res = instance.process(null);
        verify(testObj, times(1)).noArgIntRes();
        Objects.requireNonNull(res).process(mock(JsonGeneratorHelper.class));
        verify(marshal).marshal(eq(1), any(Type.class), any(JsonGeneratorHelper.class));
        verifyNoMoreInteractions(marshal);
        verifyNoMoreInteractions(testObj);
        verifyNoInteractions(unmarshal);
    }

    @Test
    public void testIntArgNoRes() {
        AutoMarshalInstance instance = subj.create(testObj, findMethod("intArgNoRes"));
        assertFalse(instance.hasResult());
        assertTrue(instance.hasArgument());
        JsonValue arg = mock(JsonValue.class);
        when(unmarshal.unmarshal(eq(arg), eq(int.class), any(JsonMarshalAnnotationSource.class))).thenReturn((int)1);
        ResultMarshaller res = instance.process(() -> arg);
        verify(testObj, times(1)).intArgNoRes(eq(1));
        verifyNoMoreInteractions(testObj);
        verifyNoInteractions(marshal);
    }

}