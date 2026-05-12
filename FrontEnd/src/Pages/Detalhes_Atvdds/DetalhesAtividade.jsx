import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { NavbarPrincipal } from '../../assets/NavBar/navbar.jsx';
import './DetalhesAtividade.css';

export function DetalhesAtividade() {
  const location = useLocation();
  const navigate = useNavigate();
  
  const atividade = location.state?.atividade;

  if (!atividade) {
    return (
      <div style={{ padding: '50px', textAlign: 'center' }}>
        <h2>Atividade não encontrada.</h2>
        <button onClick={() => navigate('/feed')}>Voltar ao Feed</button>
      </div>
    );
  }
  const lidarComCliqueSaibaMais = () => {
    if (atividade.linkExterno) {
      window.open(atividade.linkExterno, '_blank');
    } else {
      alert("O link de inscrição desta atividade ainda não está disponível.");
    }
  };

  return (
    <div className="detalhes-page">
      
      {/* Navbar inteligente: Pedimos para ela trocar o Logo pelo Voltar */}
      <NavbarPrincipal 
        mostrarBusca={true} 
        mostrarHamburger={true} 
        botaoVoltar={true} 
        acaoVoltar={() => navigate(-1)} 
      />

      <div className="detalhes-header">
        <div className="detalhes-imagem-container">
          {atividade.imagem ? (
             <img src={atividade.imagem} alt={atividade.titulo} />
          ) : (
             <div className="imagem-placeholder">📷</div>
          )}
        </div>
        
        <div className="detalhes-info-container">
          <h1 className="detalhes-titulo">
            {atividade.titulo} <span>⭐</span>
          </h1>
          <p className="detalhes-texto">📍 <strong>Local:</strong> {atividade.local}</p>
          <p className="detalhes-texto">📅 <strong>Data:</strong> {atividade.data}</p>
          <p className="detalhes-texto">💲 <strong>Valor:</strong> {atividade.valor}</p>
          
          {/* Botão com a inteligência do Link Externo */}
          <button 
            className="btn-saiba-mais header-btn"
            onClick={lidarComCliqueSaibaMais}
          >Saiba Mais</button>
        </div>
      </div>

      <div className="detalhes-body">
        <div className="detalhes-descricao-box">
          <p style={{ fontSize: '1.2rem', color: 'var(--bordas)' }}>
            Mais informações sobre esta atividade aparecerão aqui...
          </p>
          <button 
            className="btn-saiba-mais box-btn" onClick={lidarComCliqueSaibaMais}>Saiba Mais
          </button>
        </div>
      </div>

      <div className="detalhes-footer-azul"></div>
    </div>
  );
}

export default DetalhesAtividade;