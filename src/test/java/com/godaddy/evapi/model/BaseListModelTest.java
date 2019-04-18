package com.godaddy.evapi.model;

import org.junit.Test;

public class BaseListModelTest {
    
    @Test
    public void BaseListTest() {
        BaseListModel model = new BaseListModel();
        model.setCount(1);
        model.setLimit(1);
        model.setOffset(0);
        
        assert(model.getCount() == 1);
        assert(model.getLimit() == 1);
        assert(model.getOffset() == 0);
    }

}
