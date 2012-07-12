package com.xakcop.p4actions;

import java.util.ArrayList;
import java.util.List;

public class Action {

    private String name;
    private String executable;
    private String args;

    public Action() {
        this.name = "";
        this.executable = "";
        this.args = "";
    }

    public Action(String name, String executable, String args) {
        this.name = name;
        this.executable = executable;
        this.args = args;
    }

    public Action(Action action) {
        this.name = action.name;
        this.executable = action.executable;
        this.args = action.args;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public String getArguments() {
        return args;
    }

    public void setArguments(String args) {
        this.args = args;
    }

    public void setPlaceholder(String placeholder, String value) {
        args = args.replace(placeholder, value);
    }

    public List<String> parseArguments() {
        List<String> result = new ArrayList<String>();
        boolean inQuote = false;
        String token = "";
        for (int i = 0 ; i < args.length() ; i++) {
            char ch = args.charAt(i);
            if (ch == '"') {
                if (inQuote) {
                    inQuote = false;
                    if (!token.isEmpty()) {
                        result.add(token);
                        token = "";
                    }
                } else {
                    inQuote = true;
                }
            } else if (ch == ' ') {
                if (inQuote) {
                    token += ch;
                } else {
                    if (!token.isEmpty()) {
                        result.add(token);
                        token = "";
                    }
                }
            } else {
                token += ch;
            }
        }
        if (!token.isEmpty()) {
            result.add(token);
        }
        return result;
    }

    public static Action parseAction(String str) {
        String[] arr = str.split("\t");
        if (arr.length < 2) {
            throw new IllegalArgumentException("Invalid action string: " + str);
        }
        return arr.length == 3 ? new Action(arr[0], arr[1], arr[2]) : new Action(arr[0], arr[1], "");
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%s", name, executable, args);
    }
}
