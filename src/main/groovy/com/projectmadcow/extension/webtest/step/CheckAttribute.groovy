package com.projectmadcow.extension.webtest.step

import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.canoo.webtest.engine.StepFailedException
import com.gargoylesoftware.htmlunit.html.DomAttr
import org.apache.log4j.Logger

/**
 * Check for the specified attribute(s) on an element.
 */
class CheckAttribute extends AbstractMadcowStep {

    private static final Logger LOG = Logger.getLogger(CheckAttribute.class);

    String attributeName
    String attributeValue
    String htmlId
    String xpath
    String name

    /**
     * Perform the step's actual work. The minimum you need to implement.
     *
     * @throws com.canoo.webtest.engine.StepFailedException
     *          if step was not successful
     */
    public void doExecute() {

        HtmlElement element = findElement(htmlId, xpath);

        if (name != null)
            xpath = "//*[@name='${name}']"

        if (!element.isAttributeDefined(attributeName))
          throw new StepFailedException("Attribute $attributeName isn't defined for element", this)

        DomAttr attr = element.attributesMap[attributeName]
        if (attr.value != attributeValue)
          throw new StepFailedException("Attribute $attributeName with value '${attr.value}' doesn't equal expected value '$attributeValue'")
    }

    protected void verifyParameters() {
        super.verifyParameters();
        nullResponseCheck();
        paramCheck(htmlId == null && xpath == null && name == null, "\"htmlId\", \"xPath\" or \"name\" must be set!");
        paramCheck(attributeName == null || attributeValue == null, "\"attributeName\" and \"attributeValue\" must be set!");
    }


    protected Logger getLog() {
        return LOG
    }
}
