package com.example.ld_user.destdragview.eventbus;

public class PandaEventBusObject {

    private String type="";
    private  String message="";
    private Object obj;

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


    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
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
