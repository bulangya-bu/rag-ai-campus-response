package com.example.webshiyan2.config;

import com.example.webshiyan2.entity.KnowledgeDocument;
import com.example.webshiyan2.service.ModelConfigService;
import com.example.webshiyan2.service.KnowledgeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedKnowledgeBase(KnowledgeService knowledgeService) {
        return args -> knowledgeService.seedIfEmpty(List.of(
                document(
                        "图书馆借阅与超期处理",
                        "图书馆",
                        "图书借阅,续借,超期",
                        "介绍学生借阅册数、借阅期限、续借方式和超期处理规则。",
                        "本科生一次最多可借阅图书 20 册，借阅期限为 30 天，可在线续借 1 次。若图书被他人预约则不能续借。超期后系统按天记录，学生需先处理超期记录，再办理新的借阅业务。",
                        "校园图书馆服务手册"
                ),
                document(
                        "奖学金评定时间与申请材料",
                        "学生事务",
                        "奖学金,评优,材料提交",
                        "汇总奖学金评定的时间节点和常见材料。",
                        "国家奖学金和校级优秀学生奖学金一般在每年 9 月启动申报。学生需提交成绩单、综合测评材料、获奖证明和个人申请表。辅导员完成初审后，学院会进行公示。",
                        "学生工作处通知"
                ),
                document(
                        "课程补退选办理说明",
                        "教务",
                        "选课,补选,退课,教务系统",
                        "说明选课开放时间、补退选窗口和审批场景。",
                        "每学期开学第一周为集中补退选时间，学生可在教务系统自助完成普通课程的补退选。若课程涉及容量限制或培养方案冲突，需提交纸质申请并由学院教务老师审批。",
                        "教务处 FAQ"
                ),
                document(
                        "宿舍报修流程",
                        "后勤",
                        "宿舍,报修,维修,后勤",
                        "宿舍水电和基础设施问题的线上报修流程。",
                        "学生可通过校园服务大厅提交宿舍报修申请，选择楼栋、宿舍号、问题类型并上传照片。工作日 24 小时内会有维修人员联系处理；紧急断电、漏水问题可直接拨打后勤值班电话。",
                        "后勤保障中心"
                ),
                document(
                        "校园网账号与密码重置",
                        "信息化",
                        "校园网,账号,密码重置,网络",
                        "说明校园网账号开通方式和密码重置入口。",
                        "新生入学后校园网账号会与统一身份认证账号同步开通。若忘记密码，可前往信息化服务门户自助重置，或携带学生证到网络服务中心现场办理。晚间宿舍网络异常可先检查运营商认证页面。",
                        "信息化中心服务指南"
                ),
                document(
                        "毕业实习手续办理",
                        "就业",
                        "实习,三方,就业,离校",
                        "整理校外实习前的备案与安全要求。",
                        "学生参加校外实习前，应在学院完成实习去向备案，提交单位接收证明、家长知情确认和安全承诺书。涉及长期离校实习的，还需同步办理请假和宿舍备案手续。",
                        "就业指导中心"
                ),
                document(
                        "心理咨询预约流程",
                        "心理健康",
                        "心理咨询,预约,学生支持",
                        "提供心理咨询预约和紧急求助信息。",
                        "学生可通过心理中心预约系统选择咨询师和空闲时段，首次来访需填写基本问卷。若遇到紧急心理危机，可立即联系辅导员、校医院值班电话或心理中心应急热线。",
                        "大学生心理中心"
                ),
                document(
                        "校园卡补办与充值",
                        "生活服务",
                        "校园卡,补办,充值,门禁",
                        "说明校园卡挂失、补办和充值方式。",
                        "校园卡遗失后，可先在校园服务大厅进行线上挂失，再前往一卡通服务点补办新卡。食堂消费与门禁权限一般在补卡后 10 分钟内同步恢复。充值支持自助机、微信公众号和宿舍楼下充值终端。",
                        "一卡通中心"
                )
        ));
    }

    @Bean
    CommandLineRunner seedModelConfigs(ModelConfigService modelConfigService, AppProperties appProperties) {
        return args -> modelConfigService.seedDefaultConfig(appProperties.getAi());
    }

    private KnowledgeDocument document(String title,
                                       String category,
                                       String tags,
                                       String summary,
                                       String content,
                                       String source) {
        KnowledgeDocument document = new KnowledgeDocument();
        document.setTitle(title);
        document.setCategory(category);
        document.setTags(tags);
        document.setSummary(summary);
        document.setContent(content);
        document.setSource(source);
        return document;
    }
}
