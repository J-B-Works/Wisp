import './Style.css'; 
import React, { useState } from 'react';
import { NavbarPrincipal } from '../../assets/NavBar/navbar.jsx';

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
      <NavbarPrincipal mostrarBusca={true} />

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

export default Feed
