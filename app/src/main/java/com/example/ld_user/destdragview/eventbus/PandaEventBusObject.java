package com.example.ld_user.destdragview.eventbus;

public class PandaEventBusObject {

    private String type="";
    private  String message="";
    private Object obj;

    private  Object obj1;


    public PandaEventBusObject(Object obj, String message, String type) {
        this.obj = obj;
        this.message = message;
        this.type = type;
    }

    public PandaEventBusObject(Object obj, String type) {
        this.obj = obj;
        this.type = type;
    }

    public PandaEventBusObject(String message, String type) {
        this.message = message;
        this.type = type;
    }

    public PandaEventBusObject( String type) {

        this.type = type;
    }

    public PandaEventBusObject( ) {

    }

    /***
     *   DraggridView 按下
     */
    public static String SUB_DRAG_GRIDVIEW_TOUCH_EVENT_DOWN =  "sub_drag_gridview_touch_event_down";

    /***
     *   DraggridView 拖动
     */
    public static String SUB_DRAG_GRIDVIEW_TOUCH_EVENT_MOVE =  "sub_drag_gridview_touch_event_move";

    /***
     *   DraggridView 抬起
     */
    public static String SUB_DRAG_GRIDVIEW_TOUCH_EVENT_UP =  "sub_drag_gridview_touch_event_up";


    /***
     *  超出做边界
     *
     */
    public static String  OVERSTEP_LEFT_RANGE=  "overstep_left_range";

    /**
     * 超出右边界
     *
     */

    public static String  OVERSTEP_Right_RANGE=  "overstep_right_range";


    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Object getObj1() {
        return obj1;
    }

    public void setObj1(Object obj1) {
        this.obj1 = obj1;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
