/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apporiented.spring.override;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Namespace handler that collaborates with the {@link com.apporiented.spring.override.GenericBeanDefinitionParser}.
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Lars Behnke [lars.behnke at apporiented.com]
 */
public abstract class GenericNamespaceHandlerSupport extends NamespaceHandlerSupport {

	/**
	 * Registers a {@link GenericBeanDefinitionParser} for the given elementName
	 * that creates BeanDefinitions for the specified class.
     * @param elementName The xml element name.
     * @param beanClass The bean class.
     * @return The definition parser.
	 */
	protected GenericBeanDefinitionParser register(String elementName, Class<?> beanClass) {
		GenericBeanDefinitionParser parser = new GenericBeanDefinitionParser(beanClass);
		registerBeanDefinitionParser(elementName, parser);
		return parser;
	}
	
	/**
	 * Registers a {@link GenericBeanDefinitionParser} for the given elementName.
	 * The bean class is passed as string to avoid runtime dependencies. If a
	 * dependency is missing, a warning is logged and the element is ignored.
     * @param className The class name.
     * @param elementName The XML element name.
     * @return The definition parser.
	 */
	protected GenericBeanDefinitionParser register(String elementName, 
			String className) {
		
		GenericBeanDefinitionParser parser = new GenericBeanDefinitionParser(className);
		registerBeanDefinitionParser(elementName, parser);
		return parser;
	}
	
}
