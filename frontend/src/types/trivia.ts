export interface Question {
  id: string;
  question: string;
  category: string;
  answers: string[];
}

export interface CheckAnswerResponse {
  correct: boolean;
}
