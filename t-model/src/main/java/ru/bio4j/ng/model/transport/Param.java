package ru.bio4j.ng.model.transport;

//import com.thoughtworks.xstream.annotations.XStreamAlias;
//import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
//import com.thoughtworks.xstream.annotations.XStreamOmitField;
//import com.thoughtworks.xstream.exts.XStreamCDATA;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.exts.XStreamCDATA;

@XStreamAlias("param")
public class Param {

    public static enum Direction {
        UNDEFINED,
        IN,
        OUT,
        INOUT;

        public static Direction decode(String name) {
            if (name != null) {
                for (Direction type : values()) {
                    if (type.name().equals(name.toUpperCase()))
                        return type;
                }
            }
            throw new IllegalArgumentException(String.format("Unknown direction \"%s\"!", name));
        }

    }

    public static class Builder {

        private String name = null;
        private Object value = null;
        private Object innerObject = null;
        private MetaType type = MetaType.UNDEFINED;
        private int size = 0;
        private Direction direction = Direction.UNDEFINED;
        private boolean override = false;
        private String format;

        public static Param copy(Param copyFrom) {
            return new Builder()
                    .name(copyFrom.getName())
                    .value(copyFrom.getValue())
                    .innerObject(copyFrom.getInnerObject())
                    .type(copyFrom.getType())
                    .size(copyFrom.getSize())
                    .direction(copyFrom.getDirection())
                    .override(copyFrom.getOverride())
                    .format(copyFrom.getFormat())
                    .build();
        }

        public static Builder override(Param param) {
            return new Builder()
                    .name(param.getName())
                    .value(param.getValue())
                    .innerObject(param.getInnerObject())
                    .type(param.getType())
                    .size(param.getSize())
                    .direction(param.getDirection())
                    .override(param.getOverride())
                    .format(param.getFormat());
        }

        public String getName() {
            return this.name;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Object getValue() {
            return this.value;
        }

        public Builder value(Object value) {
            this.value = value;

            return this;
        }

        public Object getInnerObject() {
            return this.innerObject;
        }

        public Builder innerObject(Object innerObject) {
            this.innerObject = innerObject;
            return this;
        }

        public MetaType getType() {
            return this.type;
        }

        public Builder type(MetaType type) {
            this.type = type;
            return this;
        }

        public int getSize() {
            return this.size;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Direction getDirection() {
            return direction;
        }

        public Builder direction(Direction direction) {
            this.direction = direction;
            return this;
        }

        public boolean getOverride() {
            return this.override;
        }

        public Builder override(boolean override) {
            this.override = override;
            return this;
        }

        public String getFormat() {
            return this.format;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Param build() {
            return new Param(this);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    @XStreamAsAttribute
	private String name;

//    @XStreamCDATA
    private Object value;
    @XStreamOmitField
	private Object innerObject;
    @XStreamAsAttribute
	private MetaType type;
    @XStreamAsAttribute
	private int size;
    @XStreamAsAttribute
	private Direction direction;
    @XStreamAsAttribute
    private boolean override;
    @XStreamAsAttribute
    private String format;

    @XStreamAsAttribute
    private int id;

    public Param() { }

	public Param(Builder builder) {
		this.name = builder.getName();
		this.value = builder.getValue();
		this.innerObject = builder.getInnerObject();
		this.type = builder.getType();
		this.size = builder.getSize();
		this.direction = builder.getDirection();
        this.override = builder.getOverride();
        this.format = builder.getFormat();
	}

    public Param export() {
		return Builder.override(this).build();
	}

	public String getName() {
		return this.name;
	}
    public void setName(String value) {
        this.name = value;
    }

	public Object getValue() {
		return this.value;
	}
    public void setValue(Object value) {
        this.value = value;
    }

	public Object getInnerObject() {
		return this.innerObject;
	}
    public void setInnerObject(Object value) {
        this.innerObject = value;
    }

	public MetaType getType() {
        return this.type;
    }
    public void setType(MetaType value) {
        this.type = value;
    }

	public int getSize() {
		return this.size;
	}
    public void setSize(int value) {
        this.size = value;
    }

	public Direction getDirection() {
		return this.direction;
	}
    public void setDirection(Direction value) {
        this.direction = value;
    }

    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public boolean getOverride() {
        return override;
    }
    public void setOverride(boolean fixed) {
        this.override = fixed;
    }

    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return String.format("{ name: \"%s\", value: \"%s\" }", this.name, this.getValue());
    }
}
