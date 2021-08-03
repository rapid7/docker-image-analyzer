package com.rapid7.container.analyzer.docker.model.image.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.rapid7.container.analyzer.docker.model.HashId;
import com.rapid7.container.analyzer.docker.model.image.IdentifiablePackage;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.image.ImageId;
import com.rapid7.container.analyzer.docker.model.image.Layer;
import com.rapid7.container.analyzer.docker.model.image.LayerId;
import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import com.rapid7.container.analyzer.docker.model.image.PackageId;
import com.rapid7.container.analyzer.docker.model.image.json.mixin.IdentifiablePackageMixin;
import com.rapid7.container.analyzer.docker.model.image.json.mixin.ImageIdMixin;
import com.rapid7.container.analyzer.docker.model.image.json.mixin.ImageMixin;
import com.rapid7.container.analyzer.docker.model.image.json.mixin.LayerIdMixin;
import com.rapid7.container.analyzer.docker.model.image.json.mixin.LayerMixin;
import com.rapid7.container.analyzer.docker.model.image.json.mixin.OperatingSystemMixin;
import com.rapid7.container.analyzer.docker.model.image.json.mixin.PackageMixin;
import com.rapid7.container.analyzer.docker.model.json.mixin.HashIdMixin;
import java.io.IOException;

public class JacksonImageModelModule extends SimpleModule {

  public JacksonImageModelModule() {
    this("r7-docker-image-model-module", new Version(1, 0, 0, null, "www.rapid.com", "docker-image-model"));
  }

  public JacksonImageModelModule(String name, Version version) {
    super(name, version);
    configure(this);
  }

  public static <T extends SimpleModule> T configure(T module) {

    module.setMixInAnnotation(HashId.class, HashIdMixin.class);
    module.setMixInAnnotation(ImageId.class, ImageIdMixin.class);
    module.setMixInAnnotation(LayerId.class, LayerIdMixin.class);
    module.setMixInAnnotation(Package.class, PackageMixin.class);
    module.setMixInAnnotation(IdentifiablePackage.class, IdentifiablePackageMixin.class);
    module.setMixInAnnotation(Layer.class, LayerMixin.class);
    module.setMixInAnnotation(Image.class, ImageMixin.class);
    module.setMixInAnnotation(OperatingSystem.class, OperatingSystemMixin.class);
    module.setSerializerModifier(new BeanSerializerModifier() {

      @SuppressWarnings("unchecked")
      @Override
      public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {

        if (HashId.class.isAssignableFrom(beanDesc.getBeanClass()))
          return new HashIdSerializer((JsonSerializer<HashId>) serializer);

        return serializer;
      }
    });

    module.addKeyDeserializer(PackageId.class, new KeyDeserializer() {

      @Override
      public Object deserializeKey(String key, DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

        return new PackageId(Long.valueOf(key));
      }
    });

    return module;
  }

  public static class HashIdSerializer extends StdSerializer<HashId> {

    private JsonSerializer<HashId> defaultSerializer;

    public HashIdSerializer() {
      super(HashId.class);
    }

    public HashIdSerializer(JsonSerializer<HashId> defaultSerializer) {
      super(HashId.class);
      this.defaultSerializer = defaultSerializer;
    }

    @Override
    public void serialize(HashId value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
      gen.writeString(value.getString());
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      visitor.expectStringFormat(typeHint);
    }
  }
}
