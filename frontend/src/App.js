import React, { useState } from 'react';
import logo from './logo.svg';
import './App.css';

function App() {
  const [deck, setDeck] = useState('');
  const [result, setResult] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleCutDeck = async (e) => {
    e.preventDefault();
    
    if (!deck.trim()) {
      setError('Bitte geben Sie ein Kartendeck ein');
      return;
    }

    setLoading(true);
    setError('');
    setResult('');

    try {
      const response = await fetch(`http://localhost:8080/cut?deck=${encodeURIComponent(deck)}`);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const cutResult = await response.text();
      setResult(cutResult);
    } catch (err) {
      setError(`Fehler beim Schneiden des Decks: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <h1>Magic Deck Cutter</h1>
        
        <form onSubmit={handleCutDeck} style={{ marginTop: '20px', width: '100%', maxWidth: '500px' }}>
          <div style={{ marginBottom: '15px' }}>
            <label htmlFor="deck" style={{ display: 'block', marginBottom: '5px' }}>
              Kartendeck (Karten-IDs mit Komma getrennt):
            </label>
            <input
              id="deck"
              type="text"
              value={deck}
              onChange={(e) => setDeck(e.target.value)}
              placeholder="z.B. 1,2,3,4,5"
              style={{
                width: '100%',
                padding: '10px',
                fontSize: '16px',
                border: '1px solid #ccc',
                borderRadius: '4px',
                color: '#000'
              }}
            />
          </div>
          
          <button
            type="submit"
            disabled={loading}
            style={{
              backgroundColor: '#61dafb',
              color: '#000',
              border: 'none',
              padding: '12px 24px',
              fontSize: '16px',
              borderRadius: '4px',
              cursor: loading ? 'not-allowed' : 'pointer',
              opacity: loading ? 0.6 : 1
            }}
          >
            {loading ? 'Schneidet...' : 'Deck schneiden'}
          </button>
        </form>

        {error && (
          <div style={{
            marginTop: '20px',
            padding: '10px',
            backgroundColor: '#ff6b6b',
            color: 'white',
            borderRadius: '4px',
            maxWidth: '500px'
          }}>
            {error}
          </div>
        )}

        {result && (
          <div style={{
            marginTop: '20px',
            padding: '15px',
            backgroundColor: '#51cf66',
            color: 'white',
            borderRadius: '4px',
            maxWidth: '500px'
          }}>
            <h3>Ergebnis des Kartenschnitts:</h3>
            <p style={{ margin: '10px 0', fontSize: '18px', fontWeight: 'bold' }}>
              {result}
            </p>
          </div>
        )}
      </header>
    </div>
  );
}

export default App;
