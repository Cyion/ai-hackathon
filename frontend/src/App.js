import React, { useState } from 'react';
import logo from './logo.svg';
import './App.css';

function App() {
  const [deck, setDeck] = useState('');
  const [count, setCount] = useState(1);
  const [result, setResult] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleCutDeck = async (e) => {
    e.preventDefault();
    
    if (!deck.trim()) {
      setError('Bitte geben Sie ein Kartendeck ein');
      return;
    }

    if (count < 1) {
      setError('Die Anzahl muss mindestens 1 sein');
      return;
    }

    setLoading(true);
    setError('');
    setResult('');

    try {
      const response = await fetch(`http://localhost:8080/cut?deck=${encodeURIComponent(deck)}&count=${count}`);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const cutResult = await response.json();
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
          
          <div style={{ marginBottom: '15px' }}>
            <label htmlFor="count" style={{ display: 'block', marginBottom: '5px' }}>
              Anzahl der Schnitte:
            </label>
            <input
              id="count"
              type="number"
              value={count}
              onChange={(e) => setCount(parseInt(e.target.value) || 1)}
              min="1"
              placeholder="z.B. 3"
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
            backgroundColor: '#2c3e50',
            color: 'white',
            borderRadius: '8px',
            maxWidth: '800px',
            textAlign: 'left'
          }}>
            <h3 style={{ marginTop: 0, color: '#61dafb' }}>Ergebnis des Kartenschnitts:</h3>
            
            {/* Explanation */}
            <div style={{
              marginBottom: '20px',
              padding: '15px',
              backgroundColor: '#34495e',
              borderRadius: '6px',
              borderLeft: '4px solid #61dafb'
            }}>
              <h4 style={{ margin: '0 0 10px 0', color: '#61dafb' }}>Erklärung:</h4>
              <p style={{ margin: 0, lineHeight: '1.5' }}>{result.explanation}</p>
            </div>
            
            {/* Cards */}
            <div>
              <h4 style={{ marginBottom: '15px', color: '#61dafb' }}>Karten ({result.cards?.length || 0}):</h4>
              {result.cards?.map((card, index) => (
                <div key={card.id || index} style={{
                  marginBottom: '15px',
                  padding: '15px',
                  backgroundColor: '#34495e',
                  borderRadius: '6px',
                  border: '1px solid #4a6741',
                  display: 'flex',
                  gap: '15px'
                }}>
                  {/* Card Image */}
                  {card.imageUrl && (
                    <div style={{ flexShrink: 0 }}>
                      <img 
                        src={card.imageUrl} 
                        alt={card.name}
                        style={{
                          width: '150px',
                          height: 'auto',
                          borderRadius: '8px',
                          border: '2px solid #61dafb'
                        }}
                        onError={(e) => {
                          e.target.style.display = 'none';
                        }}
                      />
                    </div>
                  )}
                  
                  {/* Card Details */}
                  <div style={{ flex: 1 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '10px' }}>
                      <h5 style={{ margin: 0, color: '#f1c40f', fontSize: '18px' }}>{card.name}</h5>
                      <span style={{ color: '#95a5a6', fontSize: '14px' }}>ID: {card.id}</span>
                    </div>
                  
                  <div style={{ marginBottom: '8px' }}>
                    <strong style={{ color: '#e74c3c' }}>Manakosten:</strong> {card.manaCost || 'N/A'} 
                    <span style={{ marginLeft: '15px' }}>
                      <strong style={{ color: '#e74c3c' }}>CMC:</strong> {card.cmc || 'N/A'}
                    </span>
                  </div>
                  
                  <div style={{ marginBottom: '8px' }}>
                    <strong style={{ color: '#3498db' }}>Typ:</strong> {card.type || 'N/A'}
                  </div>
                  
                  {card.text && (
                    <div style={{ marginBottom: '10px' }}>
                      <strong style={{ color: '#27ae60' }}>Text:</strong>
                      <p style={{ 
                        margin: '5px 0 0 0', 
                        padding: '8px', 
                        backgroundColor: '#2c3e50',
                        borderRadius: '4px',
                        fontStyle: 'italic',
                        lineHeight: '1.4'
                      }}>
                        {card.text}
                      </p>
                    </div>
                  )}
                  
                  {card.rulings && card.rulings.length > 0 && (
                    <div>
                      <strong style={{ color: '#9b59b6' }}>Rulings:</strong>
                      <div style={{ marginTop: '5px' }}>
                        {JSON.parse(card.rulings).map((ruling, rIndex) => (
                          <div key={rIndex} style={{
                            margin: '5px 0',
                            padding: '8px',
                            backgroundColor: '#2c3e50',
                            borderRadius: '4px',
                            fontSize: '14px'
                          }}>
                            <div style={{ color: '#95a5a6', fontSize: '12px', marginBottom: '3px' }}>
                              {ruling.date}
                            </div>
                            <div style={{ lineHeight: '1.3' }}>{ruling.text}</div>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </header>
    </div>
  );
}

export default App;
