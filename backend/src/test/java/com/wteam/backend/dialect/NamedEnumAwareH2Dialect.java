package com.wteam.backend.dialect;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.spi.JdbcTypeRegistry;
import org.hibernate.type.descriptor.sql.internal.NamedNativeEnumDdlTypeImpl;
import org.hibernate.type.descriptor.sql.spi.DdlTypeRegistry;

import java.sql.Types;

public class NamedEnumAwareH2Dialect extends H2Dialect {

    @Override
    public void registerColumnTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.registerColumnTypes(typeContributions, serviceRegistry);
        
        DdlTypeRegistry ddlTypeRegistry = typeContributions.getTypeConfiguration().getDdlTypeRegistry();
        ddlTypeRegistry.addDescriptor(new NamedNativeEnumDdlTypeImpl(this));
    }

    @Override
    public JdbcType resolveSqlTypeDescriptor(
            String columnTypeName, 
            int jdbcTypeCode, 
            int precision, 
            int scale, 
            JdbcTypeRegistry jdbcTypeRegistry) {
        if (jdbcTypeCode == SqlTypes.NAMED_ENUM) {
            return jdbcTypeRegistry.getDescriptor(Types.VARCHAR);
        }
        return super.resolveSqlTypeDescriptor(columnTypeName, jdbcTypeCode, precision, scale, jdbcTypeRegistry);
    }
}
