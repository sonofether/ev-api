package com.godaddy.evapi.model;

import org.junit.Test;

public class CollisionModelTest {
    @Test
    public void CollisionModelTest() {
        CollisionModel model = new CollisionModel(false);
        assert(model.isCollision() == false);
    }
}
