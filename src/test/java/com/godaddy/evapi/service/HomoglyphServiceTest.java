package com.godaddy.evapi.service;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;

public class HomoglyphServiceTest {
    @Mock
    ITopSitesService topSitesService;
    
    @InjectMocks
    HomoglyphService hs;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void checkAlphabetsTestFalse() {
        Assert.isTrue(false == hs.containsMixedAlphabets("qQwWeErRtTyYuUiIoOpPaAsSdDfFgGhHjJkKlLzZxXcCvVbBnNmM.com"));
    }
    
    @Test
    public void checkAlphabetsTestTrue() {
        Assert.isTrue(hs.containsMixedAlphabets("yaℎoo.com"));
    }
    
    @Test
    public void checkAlphabetsTestTrueCyrillic() {
        Assert.isTrue(hs.containsMixedAlphabets("yaЧoo.com"));
    }

    @Test
    public void checkAlphabetsTestTrueArmenian() {
        Assert.isTrue(hs.containsMixedAlphabets("yaիoo.com"));
    }

    @Test
    public void checkAlphabetsTestTrueArabic() {
        Assert.isTrue(hs.containsMixedAlphabets("yaحoo.com"));
    }

    @Test
    public void checkAlphabetsTestTrueGreek() {
        Assert.isTrue(hs.containsMixedAlphabets("yaηoo.com"));
    }
    
    @Test
    public void checkAlphabetsTestTrueHebrew() {
        Assert.isTrue(hs.containsMixedAlphabets("yaאoo.com"));
    }

    @Test
    public void checkAlphabetsTestTrueMongolian() {
        Assert.isTrue(hs.containsMixedAlphabets("yaᠡoo.com"));
    }
    
    @Test
    public void checkAlphabetsTestTrueTibetan() {
        Assert.isTrue(hs.containsMixedAlphabets("yaཏoo.com"));
    }


    
    @Test
    public void convertDomaoinTest() {
        String domain = "yahoo.com";
        String result = hs.convertHomoglyphs(domain);
        Assert.isTrue(result.equals(domain));
        
        result = hs.convertHomoglyphs("yaℎoo.com");
        Assert.isTrue(result.equals(domain));
    }
    
    @Test
    public void testTopSites() {
        when(topSitesService.findById(anyString())).thenReturn(TestTopSitesService.GenerateTopSiteModel());
        when(topSitesService.findAll(anyInt(), anyInt())).thenReturn(TestTopSitesService.GenerateTopSiteListModel());
        String domain = "somefakedomain.com";
        hs.searchForTopSites(domain);

        domain = "yahoo.com";
        hs.searchForTopSites(domain);

        domain = "yaℎoo.com";
        hs.searchForTopSites(domain);

    }
}
