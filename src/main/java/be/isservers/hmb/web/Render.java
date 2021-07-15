package be.isservers.hmb.web;

abstract class Render {
    protected String upperCaseFirst(String val) {
        char[] arr = val.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        return new String(arr);
    }

    protected String surround(String tag) {
        return this.surround(tag,"");
    }
    protected String surround(String tag, String text) {
        return "<" + tag + ">" + text + "</" + tag + ">";
    }
}
