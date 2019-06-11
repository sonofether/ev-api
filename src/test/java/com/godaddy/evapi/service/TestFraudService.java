package com.godaddy.evapi.service;

import java.util.ArrayList;
import java.util.List;

import com.godaddy.evapi.model.FraudListModel;
import com.godaddy.evapi.model.FraudModel;

public class TestFraudService implements IFraudService {

    @Override
    public boolean save(FraudModel fraudModel) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean delete(String id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public FraudModel findById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FraudListModel findAll(int offset, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FraudListModel findByKeyword(String keyword, int offset, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FraudListModel findByVariableArguments(String filter, int offset, int limit) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public static FraudModel GenerateFraudModel() {
        FraudModel fraudModel = new FraudModel("google.com", 1, "Google Domain Name", "My Cool CA");
        return fraudModel;
    }
    
    public static FraudListModel GenerateFraudListModel() {
        FraudListModel fraudListModel = new FraudListModel();
        List<FraudModel> fraudList = new ArrayList<FraudModel>();
        fraudList.add(GenerateFraudModel());
        fraudList.add(new FraudModel("yahoo.com", 1, "Yahoo domain name", "My Cool CA"));
        fraudList.add(new FraudModel("citibank", 0, "Citibank", "My Cool CA"));
        
        fraudListModel.setCount(fraudList.size());
        fraudListModel.setFraudList(fraudList);
        
        return fraudListModel;
    }

}
