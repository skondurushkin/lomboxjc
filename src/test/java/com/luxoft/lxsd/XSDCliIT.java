package com.luxoft.lxsd;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author skondurushkin
 */
public class XSDCliIT {
    
    public XSDCliIT() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class XSDCli.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = {"-m", "generate", "-o", "c:/temp", "-f", "c:/temp/ndfl3.xlat", "-p", "com.luxoft.fns.taps.dispatcher.gp3.ndfl3.y2014", "c:/temp/ndfl3.xsd"};
        try {
            XSDCli.main(args);
            //new XSDCli().go(args);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        assertTrue(true);
    }
    
}
