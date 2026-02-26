import type { Question, CheckAnswerResponse } from '../types/trivia';

const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

async function getErrorMessage(res: Response): Promise<string> {
  try {
    const body = await res.json();
    if (body && typeof body.error === 'string') return body.error;
  } catch {
    //
  }
  return `Failed to fetch questions: ${res.status}`;
}

export async function fetchQuestions(amount?: number, signal?: AbortSignal): Promise<Question[]> {
  const url = amount != null
    ? `${BASE_URL}/questions?amount=${amount}`
    : `${BASE_URL}/questions`;
  const res = await fetch(url, { signal });
  if (!res.ok) {
    throw new Error(await getErrorMessage(res));
  }
  return res.json();
}

export async function checkAnswer(questionId: string, answer: string): Promise<CheckAnswerResponse> {
  const res = await fetch(`${BASE_URL}/checkanswers`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ questionId, answer }),
  });
  if (!res.ok) {
    throw new Error(`Failed to check answer: ${res.status}`);
  }
  return res.json();
}
