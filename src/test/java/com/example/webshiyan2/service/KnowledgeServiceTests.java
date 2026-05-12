package com.example.webshiyan2.service;

import com.example.webshiyan2.entity.KnowledgeDocument;
import com.example.webshiyan2.repository.KnowledgeDocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KnowledgeServiceTests {

    @Mock
    private KnowledgeDocumentRepository knowledgeDocumentRepository;

    @InjectMocks
    private KnowledgeService knowledgeService;

    @Test
    void shouldRankRelevantKnowledgeForChineseQuestion() {
        KnowledgeDocument scholarship = new KnowledgeDocument();
        scholarship.setTitle("奖学金评定时间与申请材料");
        scholarship.setCategory("学生事务");
        scholarship.setTags("奖学金,申请");
        scholarship.setSummary("奖学金申请一般在每年9月启动。");
        scholarship.setContent("学生需提交成绩单、申请表和证明材料。");

        KnowledgeDocument dorm = new KnowledgeDocument();
        dorm.setTitle("宿舍报修流程");
        dorm.setCategory("后勤");
        dorm.setTags("宿舍,报修");
        dorm.setSummary("宿舍报修需在系统填写楼栋与宿舍号。");
        dorm.setContent("工作日24小时内会有维修人员联系处理。");

        when(knowledgeDocumentRepository.findAllByOrderByUpdatedAtDesc()).thenReturn(List.of(dorm, scholarship));

        List<KnowledgeDocument> result = knowledgeService.findRelevantDocuments("奖学金申请要准备什么材料", 3);

        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getTitle()).isEqualTo("奖学金评定时间与申请材料");
    }
}
