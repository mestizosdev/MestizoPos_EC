package com.openbravo.pos.customers;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.IKeyed;
import java.io.Serializable;

/**
 *
 * @author jorgeluis
 */
public class IdentificationTypeInfo implements Serializable, IKeyed{
    
    protected String code;
    protected String name;

    public IdentificationTypeInfo(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }       
    
    public void readValues(DataRead dr) throws BasicException {
        code = dr.getString(1);
        name = dr.getString(2);
    }

    @Override
    public Object getKey() {
        return this.code;
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
