import './Style.css'
import '/src/assets/NavBar/navbar.css'
import React, { useState } from 'react';

function Feed() {

  const recomendacoes = Array.from({ length: 10 }, (_, index) => ({
    id: index + 1,
    titulo: "[Nome Da ATVDD]",
    local: "[Local]",
    data: "[Data]",
    valor: "[Valor]"
  }));

  return (
    <>
      {}
      <nav className="navbar">
        
        {/* Logo */}
        <div style={{ 
          fontWeight: 'bold', fontSize: '1.2rem', backgroundColor: 'var(--accent-azul-claro)', 
          padding: '10px 15px', borderRadius: '50px', border: '2px solid var(--bordas)' 
        }}>
          Logo
        </div>

        {/* Barra de Busca e Filtros */}
        <div style={{ display: 'flex', gap: '15px', alignItems: 'center', flex: 1, justifyContent: 'center' }}>
          <div style={{ position: 'relative', width: '50%' }}>
            <input 
              type="text" 
              placeholder="Buscar Cursos, oficinas, exposições..." 
              style={{ 
                padding: '12px 20px', borderRadius: '50px', border: '2px solid var(--bordas)', 
                width: '100%', backgroundColor: 'var(--bg-bege)', outline: 'none'
              }}
            />
            <span style={{ position: 'absolute', right: '15px', top: '10px', fontSize: '1.2rem' }}>🔍</span>
          </div>
          <button className="tag" style={{ backgroundColor: 'var(--bg-bege)', display: 'flex', alignItems: 'center', gap: '5px' }}>
            <span>🔽</span> Filtros
          </button>
        </div>

        {/* Botões de Ação (Direita) */}
        <div style={{ display: 'flex', gap: '10px' }}>
          <button style={btnAcaoStyle('var(--accent-azul-claro)')}>⭐</button>
          <button style={btnAcaoStyle('var(--card)')}>⚙️</button>
          <button style={btnAcaoStyle('var(--accent-amarelo)')}>🍔</button>
        </div>
      </nav>

      {/* 2. CONTEÚDO PRINCIPAL */}
      <div className="main-container" style={{ backgroundColor: 'var(--bg-bege)', minHeight: '100vh' }}>
        <h1 className="titulo-secao">Recomendações para você</h1>
        
        {/* TAGS COLORIDAS */}
        <div className="tags-container">
          <button className="tag" style={{ backgroundColor: 'var(--accent-laranja)', color: 'var(--bordas)' }}>Cursos</button>
          <button className="tag" style={{ backgroundColor: 'var(--accent-rosa)', color: 'var(--bordas)' }}>Oficinas</button>
          <button className="tag" style={{ backgroundColor: 'var(--accent-amarelo)', color: 'var(--bordas)' }}>Exposições</button>
          <button className="tag" style={{ backgroundColor: 'var(--accent-azul-claro)', color: 'var(--bordas)' }}>Apresentações</button>
        </div>

        {/* GRID DE CARDS */}
        <div className="card-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))' }}>
          {recomendacoes.map((item) => (
            <div className="activities-card" key={item.id}>
              
              <div className="card-imagem" style={{ backgroundColor: 'var(--cinza-imagem)', borderBottom: '2px solid var(--bordas)' }}>
                {/* O Placeholder cinza do topo do card */}
              </div>
              
              <div className="card-conteudo">
                <div className="card-cabecalho">
                  <span>{item.titulo}</span>
                  <span style={{ fontSize: '1.5rem', cursor: 'pointer' }}>⭐</span>
                </div>
                <div className="card-info">📍 {item.local}</div>
                <div className="card-info">📅 {item.data}</div>
                <div className="card-info">💲 {item.valor}</div>
              </div>

            </div>
          ))}
        </div>
      </div>
    </>
  );
}

// Função auxiliar para os botões redondos do menu superior
const btnAcaoStyle = (cor) => ({
  backgroundColor: cor,
  border: '2px solid var(--bordas)',
  borderRadius: '50%',
  width: '45px',
  height: '45px',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  cursor: 'pointer',
  fontSize: '1.2rem'
});


export default Feed
