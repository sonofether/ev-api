package com.godaddy.evapi.legalentity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class USLegalEntityTest {
    USLegalEntity usLegalEntity = new USLegalEntity();
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void validOrgNameTest() {
        Assert.assertTrue(usLegalEntity.validate("Dave's Cool Websites L.L.C."));
        Assert.assertTrue(usLegalEntity.validate("Daves's Cool Websites LLC"));
        Assert.assertTrue(usLegalEntity.validate("The Corporation for Public Broadcasting"));
        Assert.assertTrue(usLegalEntity.validate("Bank of Iowa"));
        Assert.assertTrue(usLegalEntity.validate("Association of card dealers"));
        Assert.assertTrue(usLegalEntity.validate("Institute for the Advancement of Cybernetics"));
        Assert.assertTrue(usLegalEntity.validate("The University of Toledo"));
    }
    
    @Test
    public void nullOrgNameTest() {
        Assert.assertFalse(usLegalEntity.validate(null));
    }
    
    @Test
    public void invalidOrgNameTest() {
        Assert.assertFalse(usLegalEntity.validate("Dave's Cool Websites"));
        Assert.assertFalse(usLegalEntity.validate(""));
    }

}
