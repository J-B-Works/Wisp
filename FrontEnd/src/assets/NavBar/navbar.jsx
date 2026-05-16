import React, { useState, useEffect } from 'react';
import './navbarstyle.css'; 
import { useLocation, useNavigate } from 'react-router-dom';

export function NavbarPrincipal({ mostrarBusca = true, mostrarHamburger = true, botaoVoltar = false, acaoVoltar }) {
  
  const navigate = useNavigate();
  const [termoBusca, setTermoBusca] = useState('');
  
  // 1. CRIANDO A MEMÓRIA DO MENU LATERAL (Inicia fechado: false)
  const [menuAberto, setMenuAberto] = useState(false);

  // ====================================================
  // NOVO: MEMÓRIA DO NOME DO USUÁRIO
  // ====================================================
  const [nomeUsuario, setNomeUsuario] = useState('Usuário');

  useEffect(() => {
    // Busca os dados do usuário salvos no navegador (feitos lá no Cadastro)
    const dadosSalvos = localStorage.getItem('usuarioWisp');
    if (dadosSalvos) {
      try {
        const usuarioObj = JSON.parse(dadosSalvos);
        // Se existir um nome, atualiza a variável! Se for o primeiro nome, podemos até dividir.
        if (usuarioObj.nome) {
          setNomeUsuario(usuarioObj.nome);
        }
      } catch (error) {
        console.error("Erro ao ler os dados do usuário", error);
      }
    }
  }, []);
  // ====================================================

  const executarBusca = () => {
    console.log("O utilizador pesquisou por:", termoBusca);
  };

  // 2. FUNÇÃO INTELIGENTE PARA NAVEGAR E FECHAR O MENU AO MESMO TEMPO
  const irPara = (rota) => {
    setMenuAberto(false); // Fecha o menu
    navigate(rota);       // Navega para a página
  };

  return (
    <> {/* Fragmento do React necessário quando temos elementos irmãos */}
      <nav className="navbar" style={{ backgroundColor: 'var(--barra)', borderBottom: '2px solid var(--bordas)', position: 'relative', zIndex: 10 }}>
        
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
            <button className="tag" style={{ backgroundColor: 'var(--bg-bege)', display: 'flex', alignItems: 'center', gap: '5px' }}>
              <span>🔽</span> Filtros
            </button>
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
            // 3. O BOTÃO AGORA SÓ ABRE O MENU LATERAL (muda para true)
            <button onClick={() => setMenuAberto(true)} style={btnAcaoStyle('var(--accent-amarelo)')}>🍔</button>
          )}
        </div>
      </nav>

      {/* ==================================================== */}
      {/* O MENU LATERAL (RENDERIZAÇÃO CONDICIONAL E CSS CLASS)*/}
      {/* ==================================================== */}
      
      {/* O fundo escuro: Só aparece se menuAberto for true. Se clicar nele, fecha o menu. */}
      {menuAberto && <div className="menu-backdrop" onClick={() => setMenuAberto(false)}></div>}

      {/* A Gaveta: Se menuAberto for true, adiciona a classe 'aberto' que faz ela deslizar pra tela */}
      <div className={`menu-lateral ${menuAberto ? 'aberto' : ''}`}>
        
        {/* Seta amarela para fechar */}
        <button className="btn-fechar-menu" onClick={() => setMenuAberto(false)}>&lt;</button>

        {/* Perfil Header */}
        <div className="menu-perfil">
          <div className="menu-logo-circle">Logo</div>
          {/* MÁGICA: A variável do nome entrou aqui! */}
          <span>{nomeUsuario}</span> 
        </div>

        {/* Botões do Menu (Pílulas) */}
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

        {/* Botão Sair Especial */}
        <div className="menu-item menu-btn-sair" onClick={() => irPara('/Login')}>
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