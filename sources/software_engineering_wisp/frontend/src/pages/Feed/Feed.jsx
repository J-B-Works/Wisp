import './Feed.css'; 
import { useNavigate, useLocation } from 'react-router-dom';
import React, { useState, useEffect } from 'react'; 
import { NavbarPrincipal } from '../../components/NavBar/NavBar.jsx';

export function Feed() {
  const navigate = useNavigate();
  const location = useLocation();

  const [recomendacoes, setRecomendacoes] = useState([]);
  const [carregando, setCarregando] = useState(true);

  // 1. A INTELIGÊNCIA DA ESTRELA SIMPLIFICADA
  // Em vez de true/false, guardamos um array com os IDs favoritados!
  const [favoritosIds, setFavoritosIds] = useState([]);

  // Assim que abre a tela, ele lê o localStorage e pega só os IDs
  //useEffect(() => {
  //  const salvos = JSON.parse(localStorage.getItem('favoritosWisp')) || [];
  //  setFavoritosIds(salvos.map(fav => fav.id));
  //}, []);
  
  // API de interações do usuário com os cards
  const registrarInteracaoAPI = async (atividadeId, statusFavorito) => {
    const userID = localStorage.getItem('wisp_userId');
    if (!userID) return; // Ignorando visitantes (usuários sem ID)

    try {
      await fetch(import.meta.env.VITE_API_URL + '/recomendacoes/interaction', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId: userID,
          activityId: atividadeId,
          isFavorite: statusFavorito,  // TRUE = Estrela/Favoritado | FALSE = Clique
          favorite: statusFavorito
        })
      });
    } catch (erro) {
      console.error("Erro ao registrar interação no backend:", erro);
    }
  };

  /*
  // Quando clica na estrela do card
  const lidarComEstrela = (evento, itemClicado) => {
    evento.stopPropagation(); // Impede de abrir a tela de detalhes quando clica na estrela

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

      registrarInteracaoAPI(itemClicado.id, true); // Chama API com backend p/ registrar essa interação do usuário como "favoritar"
    }

    localStorage.setItem('favoritosWisp', JSON.stringify(salvos));
  };
  */

  // Quando clica na estrela do card
  const lidarComEstrela = (evento, itemClicado) => {
    evento.stopPropagation(); // Impede de abrir a tela de detalhes quando clica na estrela

    const reverseFavoriteStatus = !itemClicado.isFavorite;

    setRecomendacoes(recomendacoes.map(item => 
      item.id === itemClicado.id ? { ...item, isFavorite: reverseFavoriteStatus } : item
    ));

    registrarInteracaoAPI(itemClicado.id, reverseFavoriteStatus); // Chama API com backend p/ registrar essa interação do usuário como "favoritar"

  };

  // Quando clica no card
  const lidarComCliqueNoCard = (item) => {
    registrarInteracaoAPI(item.id, false);                 // Chama API com backend p/ registrar essa interação do usuário como "clique"
    navigate(`/detalhes`, { state: { atividade: item } }); // Navega para página de detalhes
  };

  // 2. BUSCANDO OS DADOS (Comentado o Back, Ativado o Mock)
  useEffect(() => {
    const buscarDadosDaApi = async () => {
      setCarregando(true);
      try {
        
        const userID = localStorage.getItem('wisp_userId');

        const queryParams = new URLSearchParams(location.search); // Lê parâmetros da URL
        const busca = queryParams.get('busca');                   // Verifica se o termo "busca" aparece neles
        const categoria = queryParams.get('categoria');           // Verifica se o termo "categoria" aparece neles

        let endpoint = '';

        if (busca || categoria) {                                 // Se os dois existirem, o usuário está utilizando o filtro

          // Constrói a nova URL de busca
          const urlParams = new URLSearchParams();
          if (busca) urlParams.append('nome', busca);
          if (categoria) urlParams.append('categoria', categoria);
          if (userID) urlParams.append('userId', userID);
          
          endpoint = `${import.meta.env.VITE_API_URL}/recomendacoes/busca?${urlParams.toString()}`;

        } else if (userID) {                                      // Se NÃO é busca e NÃO é modo visitante (tem userID)
          endpoint = import.meta.env.VITE_API_URL + '/recomendacoes/' + userID; // Recomendações normais
        } else {                                                  // Se chegou aqui, só pode ser modo visitante
          endpoint = import.meta.env.VITE_API_URL + '/recomendacoes/visitante'; // Recomendações MODO VISITANTE
        }

        const resposta = await fetch(endpoint);
        const dadosJson = await resposta.json();
        setRecomendacoes(dadosJson);
        setCarregando(false);
        

      } catch (erro) {
        console.error("Erro ao conectar com a API:", erro);
        setCarregando(false);
      }
    };

    buscarDadosDaApi();
  }, [location.search]);

  return (
    <>
      <NavbarPrincipal mostrarBusca={true} mostrarHamburger={true} />

      <div className="main-container" style={{ backgroundColor: 'var(--bg-bege)', minHeight: '100vh' }}>
        <h1 className="titulo-secao">Recomendações para você</h1>
        
        <div className="tags-container">
          <button className="tag" onClick={() => navigate('/feed?categoria=Oficina')} style={{ backgroundColor: 'var(--accent-laranja)', color: 'var(--bordas)' }}>Oficina</button>
          <button className="tag" onClick={() => navigate('/feed?categoria=Música')} style={{ backgroundColor: 'var(--accent-rosa)', color: 'var(--bordas)' }}>Música</button>
          <button className="tag" onClick={() => navigate('/feed?categoria=Tecnologias+e+Artes')} style={{ backgroundColor: 'var(--accent-amarelo)', color: 'var(--bordas)' }}>Tecnologias e Artes</button>
          <button className="tag" onClick={() => navigate('/feed?categoria=Esporte+e+Atividade+Física')} style={{ backgroundColor: 'var(--accent-azul-claro)', color: 'var(--bordas)' }}>Esportes</button>
        </div>

        {carregando ? (
          <h2 style={{ textAlign: 'center', marginTop: '50px' }}>⏳ Buscando atividades...</h2>
        ) : (
          
          <div className="card-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))' }}>
            {recomendacoes.map((item) => {

              return (
                <div className="activities-card" key={item.id} style={{ cursor: 'pointer', position: 'relative' }} onClick={() => lidarComCliqueNoCard(item)}>
                  
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
                        color: item.isFavorite ? '#F6D056' : '#cccccc'
                      }}>
                      {item.isFavorite ? '⭐' : '☆'}
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