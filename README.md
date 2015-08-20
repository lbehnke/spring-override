Spring Override
===============

This library provides an XML namespace handler that allows for overriding, extending or modifying beans in a modular [Spring](http://projects.spring.io/spring-framework/) environment.
The code was originally written by [Felix Gnass](https://github.com/fgnass) and  [Carsten Woelk](https://github.com/cwoelk) back in 2007 as part of the [riot content management system](https://github.com/riotfamily/riot). So all credits go to them.

As Pivotal has [abandoned](https://eclipse.org/gemini/) the Spring DM project I figured that riot's pragmatic *override* feature is still valuable to many developers who try to modularize their Spring projects. So I decoupled the feature and moved it into its own project. Although this library comes with a Spring 4.1 dependency, it should also work with older version.
Here is a very simple example how to use it:

If you are using Maven, include the dependency into your pom.xml

    <dependency>
        <groupId>com.apporiented</groupId>
        <artifactId>spring-override</artifactId>
        <version>${spring-override.version}</version>
    </dependency>

Make sure you include the application context files of all your modules. A web project could specify the context locations like this:

    <context-param>
       <param-name>contextConfigLocation</param-name>
       <param-value>classpath*:META-INF/spring/application-context-*.xml</param-value>
    </context-param>

Let's assume you have the following configuration in your main project
(**application-context-main.xml**):

    <beans
            xmlns="http://www.springframework.org/schema/beans"
            xmlns:override="http://www.apporiented.com/schema/spring/override"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="
            http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.apporiented.com/schema/spring/override
            http://www.apporiented.com/schema/spring/override.xsd">
            
        <bean id="bean" class="com.apporiented.spring.override.OverrideTestBean">
            <property name="string" value="Original" />
            <property name="list">
                <list>
                    <value>string1</value>
                    <value>string2</value>
                    <value>string3</value>
                </list>
            </property>
            <property name="map">
                <map>
                    <entry key="key" value="original" />
                </map>
            </property>
        </bean>
    </beans>
     
Now a submodule can change application's behavior by providing a configuration such as
(**application-context-sub.xml**)   
    
    <beans
            xmlns="http://www.springframework.org/schema/beans"
            xmlns:override="http://www.apporiented.com/schema/spring/override"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="
            http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.apporiented.com/schema/spring/override
            http://www.apporiented.com/schema/spring/override.xsd">
    
        <override:properties ref="bean">
            <property name="string" value="overridden" />
        </override:properties>
    
        <override:add ref="bean" property="list" append="true">
            <value>string4</value>
            <value>string5</value>
        </override:add>
    
        <override:put ref="bean" property="map">
            <entry key="key" value="overridden" />
        </override:put>
    
    </beans>

The bean overriding occurs at a very early stage of the Spring initialization process. Hence, all autowired dependencies to **bean** will see the modifications made by the sub module.
