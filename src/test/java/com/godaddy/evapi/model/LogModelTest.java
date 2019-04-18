package com.godaddy.evapi.model;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.springframework.http.HttpStatus;

public class LogModelTest {

    @Test
    public void logTest() {
        UUID id = UUID.randomUUID();
        Date date = new Date();
        LogModel model = new LogModel("127.0.0.0", "operation name", "endpoint name", "args", "ca name", "result string", 0, 0, 0, 0);

        model.setArguments("arguments");
        model.setCa("ca");
        model.setCode(HttpStatus.OK.value());
        model.setCount(1);
        model.setEndpoint("endpoint");
        model.setId(id);
        model.setIp("127.0.0.1");
        model.setLimit(1);
        model.setOffset(1);
        model.setOperation("operation");
        model.setResult("result");
        model.setSubmitted(date);
        
        assert(model.getArguments().equals("arguments"));
        assert(model.getCa().equals("ca"));
        assert(model.getCode() == HttpStatus.OK.value());
        assert(model.getCount() == 1);
        assert(model.getEndpoint().equals("endpoint"));
        assert(model.getId() == id);
        assert(model.getIp().equals("127.0.0.1"));
        assert(model.getLimit() == 1);
        assert(model.getOffset() == 1);
        assert(model.getOperation().equals("operation"));
        assert(model.getResult().equals("result"));
        assert(model.getSubmitted() == date);
        
        try {
            assert(!LogModel.createIndex().isEmpty());
        } catch (Exception ex) {
            assert(false);
        }
        
    }
}
