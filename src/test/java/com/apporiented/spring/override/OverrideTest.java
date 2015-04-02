package com.apporiented.spring.override;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OverrideTest {
    private ClassPathXmlApplicationContext ctx;

    @Before
    public void initApplicationContext() {
        ctx = new ClassPathXmlApplicationContext("/com/apporiented/spring/override/override-application-context.xml");
    }

    @After
    public void destroyApplicationContext() {
        ctx.close();
    }

    @Test
    public void testOverrideProperty() throws Exception {
        OverrideTestBean bean = (OverrideTestBean) ctx.getBean("bean");
        Assert.assertEquals("overridden", bean.getString());
        Assert.assertEquals(5, bean.getList().size());
        Assert.assertEquals("overridden", bean.getMap().get("key"));
    }
}
