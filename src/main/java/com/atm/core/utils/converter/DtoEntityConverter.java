package com.atm.core.utils.converter;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class DtoEntityConverter {
    private ModelMapper modelMapper ;
    @Autowired
    public DtoEntityConverter(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
    public Object entityToDto(Object entitiy, Object dto){
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(entitiy, dto.getClass());
    }

    public Object dtoToEntity(Object dto, Object entity){
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(dto, entity.getClass());
    }
}
