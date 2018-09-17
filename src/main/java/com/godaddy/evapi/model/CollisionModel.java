package com.godaddy.evapi.model;


public class CollisionModel {
    private boolean collision;
    
    public CollisionModel() {
        collision = false;
    }
    
    public CollisionModel(boolean collision) {
        this.collision = collision;
    }
    
    public boolean isCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }
}
