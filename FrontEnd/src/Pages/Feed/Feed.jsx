import './Feed.css'; 
import { useNavigate } from 'react-router-dom';
import React, { useState, useEffect } from 'react'; 
import { NavbarPrincipal } from '../../assets/NavBar/navbar.jsx';

export function Feed() {
  const navigate = useNavigate();

  const [recomendacoes, setRecomendacoes] = useState([]);
  const [carregando, setCarregando] = useState(true);

  // 1. A INTELIGÊNCIA DA ESTRELA SIMPLIFICADA
  // Em vez de true/false, guardamos um array com os IDs favoritados!
  const [favoritosIds, setFavoritosIds] = useState([]);

  // Assim que abre a tela, ele lê o localStorage e pega só os IDs
  useEffect(() => {
    const salvos = JSON.parse(localStorage.getItem('favoritosWisp')) || [];
    setFavoritosIds(salvos.map(fav => fav.id));
  }, []);

  // Quando clica na estrela, a função recebe "quem" foi clicado
  const lidarComEstrela = (evento, itemClicado) => {
    evento.stopPropagation(); // MÁGICA: Impede de abrir a tela de detalhes quando clica na estrela!

    let salvos = JSON.parse(localStorage.getItem('favoritosWisp')) || [];
    const jaExiste = salvos.some(fav => fav.id === itemClicado.id);

    if (jaExiste) {
      // Tira do localStorage e da tela
      salvos = salvos.filter(fav => fav.id !== itemClicado.id);
      setFavoritosIds(favoritosIds.filter(id => id !== itemClicado.id));
    } else {
      // Coloca no localStorage e na tela
      salvos.push(itemClicado);
      setFavoritosIds([...favoritosIds, itemClicado.id]);
    }

    localStorage.setItem('favoritosWisp', JSON.stringify(salvos));
  };

  // 2. BUSCANDO OS DADOS (Comentado o Back, Ativado o Mock)
  useEffect(() => {
    const buscarDadosDaApi = async () => {
      try {
        
        const userID = localStorage.getItem('wisp_userId');
        const resposta = await fetch(import.meta.env.VITE_API_URL + '/recomendacoes/' + userID);
        const dadosJson = await resposta.json();
        setRecomendacoes(dadosJson);
        setCarregando(false);
        

      } catch (erro) {
        console.error("Erro ao conectar com a API:", erro);
        setCarregando(false);
      }
    };

    buscarDadosDaApi();
  }, []); 

  return (
    <>
      <NavbarPrincipal mostrarBusca={true} mostrarHamburger={true} />

      <div className="main-container" style={{ backgroundColor: 'var(--bg-bege)', minHeight: '100vh' }}>
        <h1 className="titulo-secao">Recomendações para você</h1>
        
        <div className="tags-container">
          <button className="tag" style={{ backgroundColor: 'var(--accent-laranja)', color: 'var(--bordas)' }}>Cursos</button>
          <button className="tag" style={{ backgroundColor: 'var(--accent-rosa)', color: 'var(--bordas)' }}>Oficinas</button>
          <button className="tag" style={{ backgroundColor: 'var(--accent-amarelo)', color: 'var(--bordas)' }}>Exposições</button>
          <button className="tag" style={{ backgroundColor: 'var(--accent-azul-claro)', color: 'var(--bordas)' }}>Apresentações</button>
        </div>

        {carregando ? (
          <h2 style={{ textAlign: 'center', marginTop: '50px' }}>⏳ Buscando atividades...</h2>
        ) : (
          
          <div className="card-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))' }}>
            {recomendacoes.map((item) => {
              
              // Verifica se ESTE item específico está no nosso array de favoritos
              const isFavorito = favoritosIds.includes(item.id);

              return (
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
                      onClick={(evento) => lidarComEstrela(evento, item)} 
                      style={{ 
                        position: 'absolute', top: '230px', right: '15px', // Coloquei ela flutuando, você pode ajustar o top/right!
                        background: 'none', border: 'none', cursor: 'pointer', fontSize: '1.8rem',
                        color: isFavorito ? '#F6D056' : '#cccccc', 
                        textShadow: isFavorito ? 'none' : '0px 0px 2px rgba(0,0,0,0.5)'
                      }}>
                      {isFavorito ? '⭐' : '☆'}
                    </button>
                    
                    {/* Coloquei o título de volta aqui também */}
                    <h3 style={{ margin: '0 0 10px 0', paddingRight: '30px' }}>{item.titulo}</h3>
                    <div className="card-info">📍 {item.local}</div>
                    <div className="card-info">📅 {item.data}</div>
                    <div className="card-info">💲 {item.valor}</div>
                  </div>

                </div>
              );
            })}
          </div>

        )}
      </div>
    </>
  );
}

export default Feed;