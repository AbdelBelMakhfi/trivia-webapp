import { useCallback, useEffect, useRef, useState } from 'react'
import { fetchQuestions, checkAnswer } from './services/api'
import type { Question } from './types/trivia'
import './App.css'

function App() {
  const [questions, setQuestions] = useState<Question[] | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [selectedAnswers, setSelectedAnswers] = useState<Record<string, string>>({})
  const [results, setResults] = useState<Record<string, boolean> | null>(null)
  const [checking, setChecking] = useState(false)
  const loadedRef = useRef(false)
  const initialLoadDoneRef = useRef(false)

  const loadQuestions = useCallback((signal?: AbortSignal) => {
    setLoading(true)
    setError(null)
    setQuestions(null)
    setSelectedAnswers({})
    setResults(null)
    setChecking(false)
    loadedRef.current = false
    fetchQuestions(5, signal)
      .then((data) => {
        loadedRef.current = true
        setQuestions(data)
      })
      .catch((e) => {
        if (e.name === 'AbortError') return
        if (!loadedRef.current) {
          setError(e instanceof Error ? e.message : 'Failed to load')
        }
      })
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    if (initialLoadDoneRef.current) return
    initialLoadDoneRef.current = true
    loadQuestions()
  }, [loadQuestions])

  function handleSelect(questionId: string, answer: string) {
    if (results != null) return
    setSelectedAnswers((prev) => ({ ...prev, [questionId]: answer }))
  }

  async function handleSubmit() {
    if (!questions?.length) return
    setChecking(true)
    setResults(null)
    const next: Record<string, boolean> = {}
    for (const q of questions) {
      const answer = selectedAnswers[q.id] ?? ''
      try {
        const { correct } = await checkAnswer(q.id, answer)
        next[q.id] = correct
      } catch {
        next[q.id] = false
      }
    }
    setResults(next)
    setChecking(false)
  }

  if (loading) return <div className="status">Loading...</div>
  if (error) return <div className="status error">{error}</div>
  if (!questions?.length) return <div className="status">No questions available.</div>

  const score = results != null ? Object.values(results).filter(Boolean).length : null

  return (
    <div className="trivia">
      <h1>Trivia</h1>
      {score != null && (
        <p className="score">Score: {score} / {questions.length}</p>
      )}
      <ul className="question-list">
        {questions.map((q) => (
          <li key={q.id} className="question">
            <p className="category">{q.category}</p>
            <p className="question-text">{q.question}</p>
            <div className="answers">
              {q.answers.map((a) => {
                const selected = selectedAnswers[q.id] === a
                const correct = results?.[q.id] === true
                const incorrect = results?.[q.id] === false
                const showCorrect = results != null && selected && correct
                const showIncorrect = results != null && selected && incorrect
                return (
                  <button
                    key={a}
                    type="button"
                    className={`answer-btn ${selected ? 'selected' : ''} ${showCorrect ? 'result-correct' : ''} ${showIncorrect ? 'result-incorrect' : ''}`}
                    onClick={() => handleSelect(q.id, a)}
                    disabled={results != null}
                  >
                    {a}
                  </button>
                )
              })}
            </div>
          </li>
        ))}
      </ul>
      {results == null && (
        <button type="button" className="submit-btn" onClick={handleSubmit} disabled={checking}>
          {checking ? 'Checking...' : 'Check answers'}
        </button>
      )}
      {results != null && (
        <button type="button" className="submit-btn" onClick={() => loadQuestions()}>
          Start again
        </button>
      )}
    </div>
  )
}

export default App
