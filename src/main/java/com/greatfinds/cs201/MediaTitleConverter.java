package com.greatfinds.cs201;

import com.greatfinds.cs201.db.MediaTitle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "mediaTitleConverter", forClass = MediaTitle.class)
public class MediaTitleConverter implements Converter<MediaTitle> {

    @Override
    public MediaTitle getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            var mediaTitle = new MediaTitle();
            mediaTitle.populateEscapedRep(value);
            return mediaTitle;
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, MediaTitle mediaTitle) {
        if (mediaTitle != null) {
            return mediaTitle.escapedSimpleRep();
        } else {
            return null;
        }
    }
}