package com.godaddy.evapi.legalentity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class CALegalEntityTest {
    CALegalEntity caLegalEntity = new CALegalEntity();
    
    @Before
    public void init() {
        ILegalEntity iLegalEntity = LegalEntityFactory.GetLegalEntity("CA");
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void validOrgNameTest() {
        Assert.assertTrue(caLegalEntity.validate("Dave's Cool Websites S.A.R.F."));
        Assert.assertTrue(caLegalEntity.validate("Dave's Cool Websites SARF"));
        Assert.assertTrue(caLegalEntity.validate("The Canadian Corporation for Public Broadcasting"));
        Assert.assertTrue(caLegalEntity.validate("Dave's limitée thing"));
        Assert.assertTrue(caLegalEntity.validate("Dave's corp"));
        Assert.assertTrue(caLegalEntity.validate("Dave's limited partnership with someone else"));
        Assert.assertTrue(caLegalEntity.validate("Dave's sole proprietorship with himself"));
    }
    
    @Test
    public void nullOrgNameTest() {
        Assert.assertFalse(caLegalEntity.validate(null));
    }
    
    @Test
    public void invalidOrgNameTest() {
        Assert.assertFalse(caLegalEntity.validate("Adam's Cool Websites"));
        Assert.assertFalse(caLegalEntity.validate(""));
    }
    
}
