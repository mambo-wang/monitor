package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@ToString
@XmlRootElement(name="storage")
@XmlAccessorType(XmlAccessType.FIELD)
public class DomainStorageDTO implements Serializable {

    private static final long serialVersionUID = -8950307639947191691L;

    /** 存储文件名。 **/
    @ApiModelProperty(value = "存储文件名")
    private String storeFile;

    /** 容量。 **/
    @ApiModelProperty(value = "容量，以MB为单位")
    private Long capacity;

    /** 取值为：ide scsi virtio usb    SCSI("scsi"),\r\n\r\n    VIRTIO("virtio"),\r\n\r\n    USB("usb");。 **/
    @ApiModelProperty(value = "取值为：ide scsi virtio usb fdc")
    private String targetBus;

    /** 取值为：ide scsi virtio usb    SCSI("scsi"),\r\n\r\n    VIRTIO("virtio"),\r\n\r\n    USB("usb");。 **/
    @ApiModelProperty(value = "取值为：ide scsi virtio usb fdc")
    private String bus;

    /**　取值为：disk cdrom floppy　。 **/
    @ApiModelProperty(value = "磁盘类型。取值为：disk cdrom floppy")
    private String diskDevice;

    public String getBus() {
        if (bus != null && !bus.isEmpty()) {
            return bus;
        }
        if (targetBus != null) {
            bus = targetBus;
        }
        return bus;

    }
}
