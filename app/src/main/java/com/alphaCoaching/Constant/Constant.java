package com.alphaCoaching.Constant;

public class Constant {
//    Collections Names:
    public static final String USER_COLLECTION = "users";
    public static final String RECENT_LECTURE_COLLECTION = "recentLectures";
    public static final String QUIZ_COLLECTION = "quiz";
    public static final String QUIZ_TAKEN_COLLECTION = "quizTaken";
    public static final String QUESTION_COLLECTION = "questions";
    public static final String QUIZ_TAKEN_QUESTION_COLLECTION = "quizTakenQuestions";
    public static final String SUBJECT_COLLECTION = "subjects";
    public static final String PDF_COLLECTION = "PDF";
    public static final String STANDARD_COLLECTION = "standard";
    public static final String VIDEO_COLLECTION = "video";
    public static final String VIDEO_CATEGORY_COLLECTION = "categoryVideo";
    public static final String NOTIFICATION_COLLECTION = "user_notifications";
    public static final String REQUESTED_USER_LIST = "requested_user_list";

    public static interface QuizCollectionFields {
        public static final String QUESTION_NUMBER = "questionNumber";
        public static final String QUIZ_DATE = "quizDate";
        public static final String QUIZ_NAME = "quizName";
        public static final String QUIZ_TIME = "quizTime";
        public static final String STANDARD = "standard";
        public static final String SUBJECT = "subject";
    }

    public static interface QuizTakenQuestionsFields {
        public static final String ATTEMPTED_ANS = "attemptedAnswer";
        public static final String QUESTION_ID = "questionId";
        public static final String QUIZ_TAKEN_ID = "quizTakenId";
        public static final String TIME_TAKEN = "timeTaken";
        public static final String QUESTION = "question";
        public static final String OPTION_1 = "option1";
        public static final String OPTION_2 = "option2";
        public static final String OPTION_3 = "option3";
        public static final String OPTION_4 = "option4";
        public static final String CORRECT_OPTION = "correctOption";
    }

    public static interface QuizTakenCollectionFields {
        public static final String QUIZ_ID = "quizId";
        public static final String SCORE = "score";
        public static final String TOTAL_SCORE = "TotalScore";
        public static final String USER_ID = "userId";
        public static final String USER_NAME = "userName";
        public static final String QUIZ_START_TIME = "quizStartTime";
        public static final String QUIZ_END_TIME = "quizEndTime";
    }

    public static interface QuestionCollectionFields {
        public static final String CORRECT_OPTION = "correctOption";
        public static final String QUIZ_ID = "quizID";
        public static final String QUESTION = "question";
        public static final String OPTION_1 = "option1";
        public static final String OPTION_2 = "option2";
        public static final String OPTION_3 = "option3";
        public static final String OPTION_4 = "option4";
        public static final String QUE_TIME = "time";
    }

    public static interface UserCollectionFields {
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String STANDARD = "standard";
        public static final String DOB = "dateOfBirth";
        public static final String EMAIL = "email";
        public static final String FIRST_TIME_LOGIN = "firstTimeLogin";
        public static final String LOGIN_STATUS = "loginStatus";
        public static final String TOKEN = "token";
    }

    public static interface RecentLectureFields {
        public static final String CHAPTER_NAME = "chapterName";
        public static final String DESCRIPTION = "description";
        public static final String LECTURE_DATE = "lectureDate";
        public static final String SUBJECT = "subject";
        public static final String STANDARD = "standard";
    }

    public static interface NotificationFields {
        public static final String STATUS = "notification_status";
        public static final String SUBJECT = "notification_subject";
        public static final String TIME = "notification_time";
        public static final String SUBJECT_URL = "subject_url";
        public static final String USER_ID = "user_id";
    }

    public static interface RequestedUserFields {
        public static final String NAME = "student_name";
        public static final String CLASS = "student_class";
        public static final String CONTACT = "student_contact_number";
        public static final String ADDRESS = "student_address";
        public static final String PERCNTAGE = "previous_class_percentage";
        public static final String REFERENCE = "reference_name";
    }

    public static interface VideoCollectionFields {
        public static final String CATEGORY = "category";
        public static final String NAME = "name";
        public static final String STANDARD = "standard";
        public static final String SUBJECT = "subject";
        public static final String URL = "url";
    }
}
