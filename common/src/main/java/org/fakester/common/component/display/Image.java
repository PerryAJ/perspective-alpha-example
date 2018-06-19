package org.fakester.common.component.display;

import com.google.gson.JsonParser;
import com.inductiveautomation.ignition.common.i18n.LocalizedString;
import com.inductiveautomation.ignition.common.jsonschema.JsonSchema;
import com.inductiveautomation.perspective.common.api.ComponentDescriptor;
import com.inductiveautomation.perspective.common.api.ComponentDescriptorImpl;
import org.fakester.common.RadComponents;


/**
 * Describes the
 */
public class Image {

    // unique ID of the component which perfectly matches that provided in the javascript's ComponentMeta implementation.
    public static String COMPONENT_ID = "rad.display.image";

    private static String SCHEMA = "    {"
                                   + "        \"type\": \"object\","
                                   + "        \"properties\": {"
                                   + "            \"url\": {"
                                   + "                \"type\": \"string\","
                                   + "                \"default\": \"/res/radcomponents/img/bananatime.gif\""
                                   + "            }"
                                   + "        }"
                                   + "    }";

    /**
     * The schema provided with the component descriptor. Use a schema instead of a plain JsonObject because it gives
     * us a little more type information, allowing the designer to highlight mismatches where it can detect them.
     */
    public static JsonSchema DEFAULT_PROPS = new JsonSchema(new JsonParser().parse(SCHEMA));

    /**
     * Components register with the Java side ComponentRegistry but providing a ComponentDescriptor.  Here we
     * build the descriptor for this one component.
     */
    public static ComponentDescriptor DESCRIPTOR = ComponentDescriptorImpl.ComponentBuilder.component()
        .withCategory("display")
        .withType(COMPONENT_ID)
        .withModuleId(RadComponents.MODULE_ID)
        .withSchema(DEFAULT_PROPS)
        .withPaletteName(LocalizedString.createRaw("Image"))
        .withResources(RadComponents.BROWSER_RESOURCES)
        .build();
}
