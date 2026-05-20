import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { NavbarPrincipal } from '../../components/NavBar/NavBar.jsx';
import './DetalhesAtividade.css';

export function DetalhesAtividade() {
  const location = useLocation();
  const navigate = useNavigate();
  
  const atividade = location.state?.atividade;
  
  const [isFav, setIsFav] = React.useState(atividade?.isFavorite === true || atividade?.favorite === true); // Estrela cinza ou amarela

  const registrarInteracaoAPI = async (statusFavorito) => {
    const userID = localStorage.getItem('wisp_userId');
    if (!userID) return;
    try {
      await fetch(import.meta.env.VITE_API_URL + '/recomendacoes/interaction', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: userID,
          activityId: atividade.id,
          isFavorite: statusFavorito,
          favorite: statusFavorito
        })
      });
    } catch (erro) { console.error("Erro:", erro); }
  };

  const lidarComEstrela = () => {
    const novoStatus = !isFav;
    setIsFav(novoStatus); // Atualiza visual na hora
    registrarInteracaoAPI(novoStatus); // Manda pro backend
  };

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
          <h1 className="detalhes-titulo" style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
            {atividade.titulo} 
            <button 
              onClick={lidarComEstrela}
              style={{
                background: 'none', border: 'none', cursor: 'pointer', fontSize: '1.8rem', padding: 0,
                color: isFav ? '#F6D056' : '#cccccc', 
                textShadow: isFav ? 'none' : '0px 0px 2px rgb(0, 0, 0)',
                color: isFav ? 'none' : '0px 0px 2px rgb(0, 0, 0)'
              }}>
              {isFav ? '⭐' : '☆'}
            </button>
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
            {atividade.descricao}
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