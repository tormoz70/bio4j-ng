package ru.bio4j.ng.commons.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
//import flexjson.JSON;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.MetaType;

import java.util.Date;

public class TBox {
    private MetaType type = MetaType.UNDEFINED;
	private String name;
//	@JSON(overrideName = "crd")
	@JsonProperty("crd")
	private Date created;
	private Double volume;
	private TPacket[] packets;
	private Exception ex;
    private BioError err;

	public TBox() {
	}

	public TBox(String name, Date created, Double volume) {
		this.setName(name);
		this.setCreated(created);
		this.setVolume(volume);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public TPacket[] getPackets() {
	    return packets;
    }

	public void setPackets(TPacket[] packets) {
	    this.packets = packets;
    }

	public Exception getEx() {
	    return ex;
    }

	public void setEx(Exception ex) {
	    this.ex = ex;
    }

    public BioError getErr() {
        return err;
    }

    public void setErr(BioError err) {
        this.err = err;
    }

    public MetaType getType() {
        return type;
    }

    public void setType(MetaType type) {
        this.type = type;
    }
}
