import React, { useState, useEffect } from 'react';
import './NavBar.css'; 
import { useLocation, useNavigate } from 'react-router-dom';

export function NavbarPrincipal({ mostrarBusca = true, mostrarHamburger = true, botaoVoltar = false, acaoVoltar }) {
  
  const navigate = useNavigate();
  const [termoBusca, setTermoBusca] = useState('');
  
  // 1. MEMÓRIAS DOS MENUS
  const [menuAberto, setMenuAberto] = useState(false);
  const [filtroAberto, setFiltroAberto] = useState(false); // <-- NOVO: Controla o dropdown de filtros

  const [nomeUsuario, setNomeUsuario] = useState('Usuário');

  useEffect(() => {
    const buscarNomeDoGrafo = async () => {
      const userID = localStorage.getItem('wisp_userId');
      if (!userID) {
        setNomeUsuario('Visitante'); // Se não tiver cadastrado, mostra que é visitante
        return;
      }

      try {
        const resposta = await fetch(`${import.meta.env.VITE_API_URL}/usuarios/${userID}`);
        if (resposta.ok) {
          const dadosJson = await resposta.json();
          if (dadosJson.nome) {
            setNomeUsuario(dadosJson.nome);
          }
        }
      } catch (error) { // Em caso de erro na API, lê o local Storage
        console.error("Erro ao buscar o nome do usuário no Grafo:", error);
        const dadosSalvos = localStorage.getItem('usuarioWisp');
        if (dadosSalvos) {
          const usuarioObj = JSON.parse(dadosSalvos);
          if (usuarioObj.nome) setNomeUsuario(usuarioObj.nome);
        }
      }
    };

    buscarNomeDoGrafo();
  }, []);

  const executarBusca = () => {
    console.log("O utilizador pesquisou por:", termoBusca);
    if (termoBusca.trim() !== '') {
      navigate(`/feed?busca=${encodeURIComponent(termoBusca)}`);
    }
  };

  const irPara = (rota) => {
    setMenuAberto(false); 
    navigate(rota);       
  };

  const selecionarFiltro = (filtroEscolhido) => {
    setFiltroAberto(false);
    console.log("Filtro selecionado para envio ao Java:", filtroEscolhido);
    if (filtroEscolhido === 'Limpar') {
      navigate('/feed');
    } else {
      navigate(`/feed?categoria=${encodeURIComponent(filtroEscolhido)}`);
    }
  };

  return (
    <> 
      <nav className="navbar" style={{ backgroundColor: 'var(--barra)', borderBottom: '2px solid var(--bordas)', position: 'sticky', top: '0', zIndex: 10 }}>
        
        {/* --- LADO ESQUERDO: INTELIGÊNCIA DE VOLTAR OU LOGO --- */}
        {botaoVoltar ? (
          <div onClick={acaoVoltar} style={{ fontWeight: '900', fontSize: '2rem', cursor: 'pointer', padding: '0 15px', color: 'var(--bordas)' }}>
            &lt;
          </div>
        ) : (
          <div onClick={() => navigate('/feed')} style={{ cursor: 'pointer', fontWeight: 'bold', fontSize: '1.2rem', backgroundColor: 'var(--accent-azul-claro)', padding: '10px 15px', borderRadius: '50px', border: '2px solid var(--bordas)' }}>
            Logo
          </div>
        )}

        {/* --- CENTRO: RENDERIZAÇÃO CONDICIONAL --- */}
        {mostrarBusca ? (
          <div style={{ display: 'flex', gap: '15px', alignItems: 'center', flex: 1, justifyContent: 'center' }}>
            <div style={{ position: 'relative', width: '50%' }}>
              <input 
                type="text" 
                placeholder="Buscar Cursos, oficinas, exposições..." 
                value={termoBusca} 
                onChange={(e) => setTermoBusca(e.target.value)} 
                onKeyDown={(e) => e.key === 'Enter' && executarBusca()} 
                style={{ padding: '12px 20px', borderRadius: '50px', border: '2px solid var(--bordas)', width: '100%', backgroundColor: 'var(--bg-bege)', outline: 'none' }}
              />
              <span onClick={executarBusca} style={{ position: 'absolute', right: '15px', top: '10px', fontSize: '1.2rem', cursor: 'pointer' }}>🔍</span>
            </div>
            
            {/* ==================================================== */}
            {/* O BOTÃO E O MENU SUSPENSO DE FILTROS                 */}
            {/* ==================================================== */}
            <div style={{ position: 'relative' }}>
              <button 
                className="tag" 
                onClick={() => setFiltroAberto(!filtroAberto)} // Abre/Fecha o menu
                style={{ backgroundColor: 'var(--bg-bege)', display: 'flex', alignItems: 'center', gap: '5px', cursor: 'pointer' }}
              >
                {/* Muda a setinha se estiver aberto ou fechado! */}
                <span>{filtroAberto ? '🔼' : '🔽'}</span> Filtros
              </button>

              {/* A CAIXINHA DO MENU SUSPENSO */}
              {filtroAberto && (
                <div style={{
                  position: 'absolute',
                  top: '120%', // Fica logo abaixo do botão
                  right: '0',  // Alinhado à direita
                  backgroundColor: 'white',
                  border: '2px solid var(--bordas)',
                  borderRadius: '12px',
                  boxShadow: '0px 8px 16px rgba(0,0,0,0.15)',
                  display: 'flex',
                  flexDirection: 'column',
                  width: '200px',
                  maxHeight: '350px',
                  overflowY: 'auto',
                  zIndex: 50 // Fica por cima de tudo no site
                }}>
                  
                  {/* Opções baseadas nas tags que você usa no Wisp */}
                  {['Ações para Cidadania', 'Alimentação', 'Artes Visuais', 'Bate-papo', 'Biblioteca', 'Cinema e Vídeo', 'Circo', 'Contação de História', 'Crianças', 'Dança', 'Esporte e Atividade Física', 'Jovens', 'Literatura', 'Meio ambiente', 'Música', 'Oficina', 'Pessoas Idosas', 'Saúde', 'Tecnologias e Artes', 'Teatro', 'Turismo'].map((opcao) => (
                    <div 
                      key={opcao}
                      onClick={() => selecionarFiltro(opcao)}
                      style={{ 
                        padding: '12px 15px', 
                        cursor: 'pointer', 
                        borderBottom: '1px solid #eee', 
                        fontWeight: '500', 
                        color: 'var(--bordas)',
                        transition: 'background-color 0.2s'
                      }}
                      onMouseEnter={(e) => e.target.style.backgroundColor = 'var(--bg-bege)'}
                      onMouseLeave={(e) => e.target.style.backgroundColor = 'white'}
                    >
                      {opcao}
                    </div>
                  ))}

                  {/* Botão de Limpar */}
                  <div 
                    onClick={() => selecionarFiltro('Limpar')}
                    style={{ 
                      padding: '12px 15px', 
                      cursor: 'pointer', 
                      backgroundColor: '#f9f9f9', 
                      color: '#B52A34', 
                      fontWeight: 'bold', 
                      textAlign: 'center' 
                    }}
                    onMouseEnter={(e) => e.target.style.backgroundColor = '#f1e6e6'}
                    onMouseLeave={(e) => e.target.style.backgroundColor = '#f9f9f9'}
                  >
                    Limpar Filtros
                  </div>

                </div>
              )}
            </div>
            {/* ==================================================== */}

          </div>
        ) : (
          <div style={{ flex: 1, textAlign: 'center', color: 'white', fontSize: '1.8rem', fontWeight: 'bold' }}>
            Wisp - Guia de Educação e Cultura
          </div>
        )}

        {/* --- LADO DIREITO: BOTÕES DE AÇÃO --- */}
        <div style={{ display: 'flex', gap: '10px' }}>
          <button onClick={() => navigate('/favoritos')} style={btnAcaoStyle('var(--accent-azul-claro)')}>⭐</button>
          <button onClick={() => navigate('/em-breve')} style={btnAcaoStyle('var(--card)')}>⚙️</button>
          {mostrarHamburger && (
            <button onClick={() => setMenuAberto(true)} style={{ ...btnAcaoStyle('var(--accent-amarelo)'), fontSize: '1.5rem', fontWeight: 'bold' }}>☰</button>)}
          {/*
          {mostrarHamburger && (
            <button onClick={() => setMenuAberto(true)} style={btnAcaoStyle('var(--accent-amarelo)')}>🍔</button>
          )}
            */}
          
        </div>
      </nav>

      {/* ==================================================== */}
      {/* O MENU LATERAL (RENDERIZAÇÃO CONDICIONAL E CSS CLASS)*/}
      {/* ==================================================== */}
      
      {menuAberto && <div className="menu-backdrop" onClick={() => setMenuAberto(false)}></div>}

      <div className={`menu-lateral ${menuAberto ? 'aberto' : ''}`}>
        
        <button className="btn-fechar-menu" onClick={() => setMenuAberto(false)}>&lt;</button>

        <div className="menu-perfil">
          <div className="menu-logo-circle">Logo</div>
          <span>{nomeUsuario}</span> 
        </div>

        <div className="menu-item" onClick={() => irPara('/perfiluc')}>
          <div className="menu-icon-circle bg-amarelo">👤</div>
          <span>Personalizar Perfil</span>
        </div>

        <div className="menu-item" onClick={() => irPara('/favoritos')}>
          <div className="menu-icon-circle bg-azul">⭐</div>
          <span>Favoritos</span>
        </div>

        <div className="menu-item" onClick={() => irPara('/em-breve')}>
          <div className="menu-icon-circle bg-laranja">🗄️</div>
          <span>Transparência</span>
        </div>

        <div className="menu-item" onClick={() => irPara('/em-breve')}>
          <div className="menu-icon-circle bg-rosa">ℹ️</div>
          <span>Sobre</span>
        </div>

        <div className="menu-item" onClick={() => irPara('/em-breve')}>
          <div className="menu-icon-circle bg-branco">⚙️</div>
          <span>Configurações</span>
        </div>

        {/* Função de Logout Atualizada */}
        <div className="menu-item menu-btn-sair" onClick={() => {
            localStorage.removeItem('usuarioWisp');
            localStorage.removeItem('wisp_userId');
            irPara('/Login');
        }}>
          <div className="menu-icon-circle bg-vermelho" style={{border: 'none'}}>🚪</div>
          <span style={{color: 'white', marginLeft: '10px'}}>Sair</span>
        </div>

      </div>
    </>
  );
}

// Função auxiliar
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