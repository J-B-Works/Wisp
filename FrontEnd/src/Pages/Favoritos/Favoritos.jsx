import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { NavbarPrincipal } from '../../assets/NavBar/navbar.jsx';
// Importando o CSS do Feed para reaproveitar tudo!
import '../Feed/Feed.css'; 

export function Favoritos() {
  const navigate = useNavigate();
  const [listaFavoritos, setListaFavoritos] = useState([]);

  // Busca os favoritos salvos assim que a tela abre
  useEffect(() => {
    const salvos = JSON.parse(localStorage.getItem('favoritosWisp')) || [];
    setListaFavoritos(salvos);
  }, []);

  // Remover favorito ao clicar na estrela
  const removerFavorito = (evento, idParaRemover) => {
    evento.stopPropagation(); 
    
    const novaLista = listaFavoritos.filter(fav => fav.id !== idParaRemover);
    
    setListaFavoritos(novaLista);
    localStorage.setItem('favoritosWisp', JSON.stringify(novaLista));
  };

  return (
    <>
      <NavbarPrincipal mostrarBusca={true} mostrarHamburger={true} />

      {/* Mesma div e mesmos estilos inline que você usa no Feed! */}
      <div className="main-container" style={{ backgroundColor: 'var(--bg-bege)', minHeight: '100vh' }}>
        
        <h1 className="titulo-secao" style={{ color: 'black' }}>Marcados como favoritos:</h1>
        
        {/* TAGS COLORIDAS */}
        <div className="tags-container" style={{ marginBottom: '40px' }}>
          <button className="tag" style={{ backgroundColor: 'var(--accent-laranja)', color: 'var(--bordas)' }}>Cursos</button>
          <button className="tag" style={{ backgroundColor: 'var(--accent-rosa)', color: 'var(--bordas)' }}>Oficinas</button>
          <button className="tag" style={{ backgroundColor: 'var(--accent-amarelo)', color: 'var(--bordas)' }}>Exposições</button>
          <button className="tag" style={{ backgroundColor: 'var(--accent-azul-claro)', color: 'var(--bordas)' }}>Apresentações</button>
        </div>

        {listaFavoritos.length === 0 ? (
          <h2 style={{ textAlign: 'center', marginTop: '50px', color: 'var(--bordas)' }}>
            Você ainda não salvou nenhuma atividade!
          </h2>
        ) : (
          
          /* GRID E CARDS IDÊNTICOS AO FEED */
          <div className="card-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))' }}>
            {listaFavoritos.map((item) => (
              
              <div className="activities-card" key={item.id} style={{ cursor: 'pointer', position: 'relative' }} onClick={() => navigate(`/detalhes`, { state: { atividade: item } })}>
                
                <div className="card-imagem" style={{ backgroundColor: 'var(--cinza-imagem)', borderBottom: '2px solid var(--bordas)', height: '220px', overflow: 'hidden' }}>
                  {item.imagem ? (
                    <img src={item.imagem} alt={item.titulo} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                  ) : (
                    <div style={{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#666', fontSize: '2rem' }}>📷</div>
                  )}
                </div>
                
                <div className="card-conteudo">
                  
                  {/* BOTÃO DA ESTRELA */}
                  <button 
                    onClick={(evento) => removerFavorito(evento, item.id)} 
                    style={{ 
                      position: 'absolute', top: '230px', right: '15px', 
                      background: 'none', border: 'none', cursor: 'pointer', fontSize: '1.8rem',
                      color: '#F6D056'
                    }}>
                    ⭐
                  </button>
                  
                  <h3 style={{ margin: '0 0 10px 0', paddingRight: '30px' }}>{item.titulo}</h3>
                  <div className="card-info">📍 {item.local}</div>
                  <div className="card-info">📅 {item.data}</div>
                  <div className="card-info">💲 {item.valor}</div>
                </div>

              </div>
            ))}
          </div>

        )}
      </div>
    </>
  );
}

export default Favoritos;