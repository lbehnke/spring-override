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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Namespace handler that handles the <code>override</code> namespace.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class OverrideNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("if-present", new ConditionalParser());
		registerBeanDefinitionParser("properties", new PropertyOverrideParser());
		registerBeanDefinitionParser("put", new MapMergeParser());
		registerBeanDefinitionParser("add", new ListMergeParser());
		registerBeanDefinitionParser("bean", new BeanOverrideParser());
	}
	
	private static class ConditionalParser implements BeanDefinitionParser {

		public BeanDefinition parse(Element element, ParserContext parserContext) {
			BeanDefinitionParserDelegate delegate = parserContext.getDelegate();
			BeanDefinitionRegistry registry = parserContext.getRegistry();
			String ref = element.getAttribute("ref");
			if (registry.containsBeanDefinition(ref)) {
				for (Element child : getChildElements(element)) {
					if (delegate.isDefaultNamespace(child.getNamespaceURI())) {
						BeanDefinitionHolder bdh = delegate.parseBeanDefinitionElement(child);
						String id = bdh.getBeanName();
						if (id != null) {
							registry.registerBeanDefinition(id, bdh.getBeanDefinition());
						}
					}
					else {
						delegate.parseCustomElement(child);
					}
				}
			}
			return null;
		}

        public List<Element> getChildElements(Element ele) {
            NodeList nl = ele.getChildNodes();
            List<Element> childElems = new LinkedList<Element>();
            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                if (node instanceof Element) {
                    childElems.add((Element) node);
                }
            }
            return childElems;
        }
		
	}
	
	private static class PropertyOverrideParser extends GenericBeanDefinitionParser {
		
		public PropertyOverrideParser() {
			super(PropertyOverrideProcessor.class);
		}
		
		@Override
		protected void postProcess(BeanDefinitionBuilder beanDefinition,
				ParserContext parserContext, Element element) {
			
			BeanDefinition bd = new RootBeanDefinition();
			parserContext.getDelegate().parsePropertyElements(element, bd);
			beanDefinition.addPropertyValue("propertyValues", bd.getPropertyValues());
		}
	}
	
	private static class MapMergeParser extends GenericBeanDefinitionParser {
		
		public MapMergeParser() {
			super(MapMergeProcessor.class);
		}
		
		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected void postProcess(BeanDefinitionBuilder beanDefinition,
				ParserContext parserContext, Element element) {
			
			Map entries = parserContext.getDelegate().parseMapElement(
					element, beanDefinition.getBeanDefinition());
			
			// The parsed Map is a ManagedMap. We put the values into a
			// HashMap so that the reference resolution is deferred until
			// the actual target bean is initialized.
			beanDefinition.addPropertyValue("entries", new HashMap(entries));
		}
	}
	
	private static class ListMergeParser extends GenericBeanDefinitionParser {
		
		public ListMergeParser() {
			super(ListMergeProcessor.class);
		}
		
		@Override
		protected void postProcess(BeanDefinitionBuilder beanDefinition,
				ParserContext parserContext, Element element) {
			
			List<?> values = parserContext.getDelegate().parseListElement(
					element, beanDefinition.getBeanDefinition());
			
			// The parsed List is a ManagedList. We put the values into an
			// ArrayList so that the reference resolution is deferred until
			// the actual target bean is initialized.
			beanDefinition.addPropertyValue("values", new ArrayList<Object>(values));
		}
	}

	private static class BeanOverrideParser extends GenericBeanDefinitionParser {
		
		public BeanOverrideParser() {
			super(BeanOverrideProcessor.class);
		}
		
		@Override
		protected boolean isEligibleAttribute(String attributeName, 
				ParserContext parserContext) {
			
			return attributeName.equals("ref") 
					|| attributeName.equals("merge")
					|| attributeName.equals("order");
		}
		
		@Override
		protected void postProcess(BeanDefinitionBuilder builder,
				ParserContext parserContext, Element element) {
			
			BeanReplacement replacement = new BeanReplacement(
					parserContext.getDelegate().parseBeanDefinitionElement(
					element, null, builder.getBeanDefinition()));
			
			builder.addPropertyValue("beanReplacement", replacement);
			builder.getBeanDefinition().getPropertyValues().setConverted();
		}
	}
	
	static class BeanReplacement {
		
		private BeanDefinition beanDefinition;

		public BeanReplacement(BeanDefinition beanDefinition) {
			this.beanDefinition = beanDefinition;
		}

		public BeanDefinition getBeanDefinition() {
			return this.beanDefinition;
		}
		
	}
	
}
