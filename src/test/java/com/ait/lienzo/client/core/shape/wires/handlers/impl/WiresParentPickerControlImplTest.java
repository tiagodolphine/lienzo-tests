package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresParentPickerControlImplTest {

    private static final double START_X = 3;
    private static final double START_Y = 5;

    @Mock
    private ColorMapBackedPicker picker;

    private ColorMapBackedPicker.PickerOptions pickerOptions = new ColorMapBackedPicker.PickerOptions(false, 0);
    private WiresParentPickerControlImpl tested;
    private Layer layer;
    private WiresManager manager;
    private WiresShape shape;
    private WiresShape parent;

    @Before
    public void setup()
    {
        layer = new Layer();
        manager = WiresManager.get(layer);
        shape = new WiresShape(new MultiPath().rect(0, 0, 10, 10));
        shape.setWiresManager(manager);
        parent = new WiresShape(new MultiPath().rect(0, 0, 100, 100));
        parent.setWiresManager(manager);
        tested = new WiresParentPickerControlImpl(new WiresShapeLocationControlImpl(shape),
                                                  new WiresParentPickerControlImpl.ColorMapBackedPickerProvider() {
                                                      @Override
                                                      public ColorMapBackedPicker get(WiresLayer layer) {
                                                          return picker;
                                                      }

                                                      @Override
                                                      public ColorMapBackedPicker.PickerOptions getOptions() {
                                                          return pickerOptions;
                                                      }
                                                  });
    }

    @Test
    public void testReturnParentAtCertainLocation()
    {
        // Start moving shape.
        tested.onMoveStart(START_X,
                           START_Y);
        assertEquals(manager.getLayer(), tested.getParent());
        // Mock find method to return parent at the following location.
        when(picker.findShapeAt(eq((int) (START_X + 10)),
                                eq((int) (START_Y + 10))))
                .thenReturn(new PickerPart(parent, PickerPart.ShapePart.BODY));

        // Move step. Parent is here.
        double dx = 10d;
        double dy = 10d;
        tested.onMove(dx, dy);
        assertEquals(parent, tested.getParent());

        // Move step. Parent no here.
        dx = -10d;
        dy = -10d;
        tested.onMove(dx, dy);
        assertEquals(manager.getLayer(), tested.getParent());
    }
}
