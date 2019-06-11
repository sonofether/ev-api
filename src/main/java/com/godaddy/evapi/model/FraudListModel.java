package com.godaddy.evapi.model;

import java.util.ArrayList;
import java.util.List;

public class FraudListModel extends BaseListModel {
    private List<FraudModel> fraudList;
    
    public FraudListModel() {
        fraudList = new ArrayList<FraudModel>();
    }

    
    public List<FraudModel> getFraudList() {
        return fraudList;
    }

    
    public void setFraudList(List<FraudModel> fraudList) {
        this.fraudList = fraudList;
    }
    
    
}
