package jackberg.metric.benchmark;

import io.micrometer.core.instrument.Tag;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class AttributesHolder {

  static final AttributeKey<String> ATTRIBUTE_KEY = AttributeKey.stringKey("key");

  final Attributes[] attributesList;
  final List<Tag>[] tagsList;
  final String[] labelValues;

  AttributesHolder(int cardinality) {
    Random random = new Random();
    attributesList = new Attributes[cardinality];
    tagsList = new List[cardinality];
    labelValues = new String[cardinality];
    String last = "aaaaaaaaaaaaaaaaaaaaaaaaaa";
    for (int i = 0; i < cardinality; i++) {
      char[] chars = last.toCharArray();
      chars[random.nextInt(last.length())] = (char) (random.nextInt(26) + 'a');
      last = new String(chars);
      attributesList[i] = Attributes.builder().put(ATTRIBUTE_KEY, last).build();
      tagsList[i] = Collections.singletonList(Tag.of("key", last));
      labelValues[i] = last;
    }
  }
}
