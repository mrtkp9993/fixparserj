package com.muratkoptur.fixparserj;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.InputStream;

import quickfix.DefaultMessageFactory;
import quickfix.Field;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.MessageUtils;
import quickfix.field.ApplVerID;
import quickfix.DataDictionary;
import quickfix.ConfigError;

public class Parser {
    private static DataDictionary dataDictionary;
    
    static {
        initializeDataDictionary();
    }

    public static ParseResult parse(String fixString) throws InvalidMessage {
        if (fixString == null || fixString.trim().isEmpty()) {
            throw new IllegalArgumentException("Input string is null or empty");
        }

        try {
            String normalizedFixString = normalizeFixMessage(fixString);

            Message message = new Message(normalizedFixString);
            Map<String, String> fields = new HashMap<>();
            
            Iterator<Field<?>> headerIterator = message.getHeader().iterator();
            while (headerIterator.hasNext()) {
                int tag = headerIterator.next().getTag();
                try {
                    String value = message.getHeader().getString(tag);
                    fields.put(String.valueOf(tag), value);
                } catch (Exception e) {
                    fields.put(String.valueOf(tag), "[unable to read]");
                }
            }

            Iterator<Field<?>> bodyIterator = message.iterator();
            while (bodyIterator.hasNext()) {
                int tag = bodyIterator.next().getTag();
                try {
                    String value = message.getString(tag);
                    fields.put(String.valueOf(tag), value);
                } catch (Exception e) {
                    fields.put(String.valueOf(tag), "[unable to read]");
                }
            }

            Iterator<Field<?>> trailerIterator = message.getTrailer().iterator();
            while (trailerIterator.hasNext()) {
                int tag = trailerIterator.next().getTag();
                try {
                    String value = message.getTrailer().getString(tag);
                    fields.put(String.valueOf(tag), value);
                } catch (Exception e) {
                    fields.put(String.valueOf(tag), "[unable to read]");
                }
            }

            return new ParseResult(true, null, fields);
            
        } catch (Exception e) {
            return new ParseResult(false, "Failed to parse FIX message: " + e.getMessage(), null);
        }
    }

    private static String normalizeFixMessage(String fixString) {
        String normalized = fixString;
        
        normalized = normalized.replace("|", "\u0001");
        normalized = normalized.replace("^", "\u0001");
        normalized = normalized.replace("␁", "\u0001");
        normalized = normalized.replace("\\001", "\u0001");
        normalized = normalized.replace("<SOH>", "\u0001");
        
        if (!normalized.endsWith("\u0001")) {
            normalized += "\u0001";
        }
        
        return normalized;
    }
    
    private static void initializeDataDictionary() {
        try {
            InputStream ddStream = Parser.class.getClassLoader().getResourceAsStream("FIX50.xml");
            if (ddStream != null) {
                dataDictionary = new DataDictionary(ddStream);
                System.out.println("Successfully loaded data dictionary");
            } else {
                System.out.println("Warning: data dictionary not found");
                dataDictionary = null;
            }
        } catch (ConfigError e) {
            System.err.println("Error loading data dictionary: " + e.getMessage());
            dataDictionary = null;
        }
    }
    
    public static String getFieldName(String tag) {
        if (dataDictionary != null) {
            try {
                int tagNum = Integer.parseInt(tag);
                String fieldName = dataDictionary.getFieldName(tagNum);
                if (fieldName != null && !fieldName.isEmpty()) {
                    return fieldName;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid tag: " + tag + " - " + e.getMessage());
            }
        }
        
        return "Tag_" + tag;
    }
    
    public static String getFieldValueWithEnum(String tag, String value) {
        if (dataDictionary != null && value != null && !value.isEmpty()) {
            try {
                int tagNum = Integer.parseInt(tag);
                String enumValue = dataDictionary.getValueName(tagNum, value);
                if (enumValue != null && !enumValue.isEmpty()) {
                    return value + " - " + enumValue;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid tag: " + tag + " - " + e.getMessage());
            } catch (Exception e) {
                // Field might not have enums or other error, just return original value
            }
        }
        
        return value;
    }

    public static class ParseResult {
        private final boolean success;
        private final String error;
        private final Map<String, String> fields;

        public ParseResult(boolean success, String error, Map<String, String> fields) {
            this.success = success;
            this.error = error;
            this.fields = fields;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getError() {
            return error;
        }

        public Map<String,String> getFields() {
            return fields;
        }
    }
}
