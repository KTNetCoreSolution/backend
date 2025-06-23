package com.boot.ktn.controller.sample;

import com.boot.ktn.entity.sample.MybaitsClassMethodEntity;
import com.boot.ktn.entity.sample.MybaitsXmlMethodEntity;
import com.boot.ktn.mapper.sample.MybaitsXmlMethodMapper;
import com.boot.ktn.service.sample.MybaitsClassMethodService;
import com.boot.ktn.util.CommonApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path}/public/sample")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Sample MyBatis", description = "Endpoints for sample MyBatis queries")
public class MybaitsClassMethodController {
    private final MybaitsClassMethodService service;

    private final MybaitsXmlMethodMapper mybaitsXmlMethodMapper;

    @CommonApiResponses
    @GetMapping("/find/{id}")
    public MybaitsClassMethodEntity findById(@PathVariable String id) {
        return service.findById(id);
    }

    @CommonApiResponses
    @GetMapping("/findByIdXml")
    public MybaitsXmlMethodEntity findByIdXml(
            @RequestParam("userId") String userId) {
        return mybaitsXmlMethodMapper.findByIdXml(userId);
    }
}