package com.example.usermanagement.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSyllabusOutlineRequest {
    String[] trainingProgramName;
    String topicName;
    String topicOutline;
    String version;
    String technicalGroup;
    String priority;
    String courseObjective;
    String publishStatus = "inactive";
    String trainingPrinciples;
    int trainingAudience;
    String assignmentLab = "0%";
    String conceptLecture = "0%";
    String guideReview = "0%";
    String testQuiz = "0%";
    String exam = "0%";
    String quiz = "0%";
    String assignment = "0%";
    String finalValue = "0%";
    String finalTheory = "0%";
    String finalPractice = "0%";
    String gpa = "0%";
    String[] outputCode;
    List<DayDTO> syllabus;
}
