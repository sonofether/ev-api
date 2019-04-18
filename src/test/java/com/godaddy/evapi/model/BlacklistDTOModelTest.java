package com.godaddy.evapi.model;

import org.junit.Test;

public class BlacklistDTOModelTest {
    
    @Test
    public void BlacklistDTOModelTest() {
        BlacklistDTOModel model = new BlacklistDTOModel(true);
        assert(model.isBlacklisted());        
    }
}
