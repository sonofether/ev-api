package com.godaddy.evapi.model;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("serial")
public class BaseModel implements Serializable {
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T toObject(String json) throws Exception {
        return (T) new ObjectMapper().readValue(json, this.getClass());
    }

    public String toJson() {
        try {
            return toJsonInternal();
        } catch (Exception e) {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T> T fromXml(String xml) {
        return (T) new XStream().fromXML(xml);
    }

    @SuppressWarnings("unchecked")
    public <T> T fromXml(String xml, Map<String, Class> aliasMap) {
        if(aliasMap == null) return fromXml(xml);

        XStream xstream = new XStream();
        for(String key : aliasMap.keySet()) {
            xstream.alias(key, aliasMap.get(key));
        }
        return (T) xstream.fromXML(xml);
    }

    public String toXml() {
        return new XStream(new DomDriver()).toXML(this);
    }

    private String toJsonInternal() throws Exception {
        ObjectWriter writer = new ObjectMapper().writer();
        writer.withDefaultPrettyPrinter();
        return writer.writeValueAsString(this);
    }
}
