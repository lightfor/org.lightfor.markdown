package org.lightfor.markdown.flexmark;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ast.AutoLink;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.html.AttributeProvider;
import com.vladsch.flexmark.html.AttributeProviderFactory;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.IndependentAttributeProviderFactory;
import com.vladsch.flexmark.html.renderer.AttributablePart;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.html.Attributes;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.util.Arrays;

/**
 * Created by Light on 2017/3/9.
 */
public class AttributeProviderSample {
    static class SampleExtension implements HtmlRenderer.HtmlRendererExtension {
        @Override
        public void rendererOptions(final MutableDataHolder options) {
            // add any configuration settings to options you want to apply to everything, here
        }

        @Override
        public void extend(final HtmlRenderer.Builder rendererBuilder, final String rendererType) {
            rendererBuilder.attributeProviderFactory(SampleAttributeProvider.Factory());
        }

        static SampleExtension create() {
            return new SampleExtension();
        }
    }

    static class SampleAttributeProvider implements AttributeProvider {
        @Override
        public void setAttributes(final Node node, final AttributablePart part, final Attributes attributes) {
            if (node instanceof AutoLink && part == AttributablePart.LINK) {
                // Put info in custom attribute instead
                attributes.replaceValue("class", "my-autolink-class");
            }
        }

        static AttributeProviderFactory Factory() {
            return new IndependentAttributeProviderFactory() {
                @Override
                public AttributeProvider create(NodeRendererContext context) {
                    //noinspection ReturnOfInnerClass
                    return new SampleAttributeProvider();
                }
            };
        }
    }

    static String commonMark(String markdown) {
        MutableDataHolder options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(new Extension[] { AutolinkExtension.create(), SampleExtension.create() }));

        // change soft break to hard break
        options.set(HtmlRenderer.SOFT_BREAK, "<br/>");

        Parser parser = Parser.builder(options).build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        return renderer.render(document);
    }

    public static void main(String[] args) {
        String html = commonMark("http://github.com/vsch/flexmark-java");
        System.out.println(html); // output: <p><a href="http://github.com/vsch/flexmark-java" class="my-autolink-class">http://github.com/vsch/flexmark-java</a></p>

        html = commonMark("hello\nworld");
        System.out.println(html); // output: <p>hello<br/>world</p>
    }
}
