import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../index.css'; 
import './Cadastro.css'; 
import { NavbarPrincipal } from '../../assets/NavBar/navbar.jsx'; 

export function Cadastro() {
  const navigate = useNavigate();
  const [passoAtual, setPassoAtual] = useState(1);

  // 1. MEMÓRIA: Adicionamos cnpj e numero para a Instituição
  const [dadosUsuario, setDadosUsuario] = useState({
    tipoUsuario: '', 
    nome: '', 
    idade: '', 
    cep: '',
    email: '', 
    confirmarEmail: '',
    senha: '', 
    confirmarSenha: '', 
    cnpj: '',   // <--- NOVO
    numero: '', // <--- NOVO
    interesses: []
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setDadosUsuario({ ...dadosUsuario, [name]: value });
  };

  const escolherTipoEAvancar = (tipoEscolhido) => {
    setDadosUsuario({ ...dadosUsuario, tipoUsuario: tipoEscolhido });
    setPassoAtual(2);
  };

  const voltar = () => {
    if (passoAtual === 1) navigate(-1); 
    else setPassoAtual(passoAtual - 1);
  };

  // Lógica inteligente do botão Avançar/Confirmar
  const avancarFormulario = () => { // TODO [DUVIDA] Bruna - Esse seria o envio de dados da instituição enquanto o finalizarCadastro() é o envio de dados do usuário comum?
    if (dadosUsuario.tipoUsuario === 'instituicao') {
      // Se for instituição, acabou o cadastro! Manda pro Feed.
      console.log("Dados finais enviados para o Banco de Dados:", dadosUsuario);
      navigate('/feed');
    } else {
      // Se for usuário comum, vai pro Passo 3 (Quiz)
      setPassoAtual(passoAtual + 1);
    }
  };

  // Função para marcar e desmarcar os botões do Quiz
  const toggleInteresse = (interesseClicado) => {
    setDadosUsuario((dadosAntigos) => {
      const listaInteresses = dadosAntigos.interesses;
      // Se já tem na lista, tira. Se não tem, coloca.
      if (listaInteresses.includes(interesseClicado)) {
        return { ...dadosAntigos, interesses: listaInteresses.filter(i => i !== interesseClicado) };
      } else {
        return { ...dadosAntigos, interesses: [...listaInteresses, interesseClicado] };
      }
    });
  };
  /* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  BRUNA, PODE MUDAR AS CATEGORIAS AQUI, PODE ADICIONAR OU REMOVER, SÃO AUTOMÁTICAS
  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  */
  // Memória vazia para preencher com as categorias existentes
  const [opcoesInteressesEspecificos, setOpcoesInteressesEspecificos] = useState([]);

  useEffect(() => {
    const buscarCategorias = async () => {
      try {
        // REACT ---> JAVA
        const resposta = await fetch('http://localhost:8080/api/categorias');
        // REACT <--- JAVA
        const dados = await resposta.json();
        setOpcoesInteressesEspecificos(dados); // Preenche categorias existentes
      } catch (erro) {
        console.error("Erro ao carregar categorias do Grafo:", erro);
      }
    };

    buscarCategorias();
  }, []);

  // Função finalizadora (Passo 4 do comum)
  const finalizarCadastro = async () => {
    console.log("MÁGICA FEITA! Dados enviados para o Banco de Dados:", dadosUsuario);
    
    // REACT ---> JAVA
    const resposta = await fetch('http://localhost:8080/api/usuarios/cadastrar', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(dadosUsuario)
    });

    // REACT <--- JAVA
    const idDoUsuario = await resposta.text();
    console.log("Usuário cadastrado com sucesso! ID do vértice desse usuário no Grafo:", idDoUsuario);

    localStorage.setItem('wisp_userId', idDoUsuario); // SALVA ID DO USUÁRIO NA MEMÓRIA DO NAVEGADOR, necessário pra página Feed saber p/ qual usuário solicitar recomendações

    navigate('/feed'); // Leva para o feed
  };

  return (
    <div className="cadastro-page">
      {/* Navbar com a coroa de ondinhas será controlada no seu arquivo principal de Navbar se desejar */}
      <NavbarPrincipal mostrarBusca={false} mostrarHamburger={false} />

      <div className="main-container cadastro-content">

        {/* ========================================================= */}
        {/* PASSO 1: VOCÊ É... */}
        {/* ========================================================= */}
        {passoAtual === 1 && (
          <>
            <h1 className="cadastro-title">Você é...</h1>
            <div className="cadastro-botoes-container">
              <button className="btn-cadastro btn-comum" onClick={() => escolherTipoEAvancar('comum')}>Usuário comum</button>
              <button className="btn-cadastro btn-instituicao" onClick={() => escolherTipoEAvancar('instituicao')}>Instituição</button>
              <button className="btn-cadastro btn-voltar" onClick={voltar}>Voltar</button>
            </div>
          </>
        )}

        {/* ========================================================= */}
        {/* PASSO 2: USUÁRIO COMUM */}
        {/* Só aparece se estiver no passo 2 E for usuário comum */}
        {/* ========================================================= */}
        {passoAtual === 2 && dadosUsuario.tipoUsuario === 'comum' && (
          <>
            <h1 className="cadastro-title" style={{ color: '#2CB3D4' }}>Cadastro de usuário</h1>
            <div className="form-container">
              
              <div className="input-group">
                <label>Nome:</label>
                <input type="text" name="nome" value={dadosUsuario.nome} onChange={handleChange} className="cadastro-input" />
              </div>

              <div className="input-row" style={{ justifyContent: 'center' }}>
                <div className="input-group" style={{ flex: 'none' }}>
                <label>Idade:</label>
                <select 
                    name="idade" 
                    value={dadosUsuario.idade} 
                    onChange={handleChange} 
                    className="cadastro-input input-small" 
                    style={{ cursor: 'pointer' }} // Adiciona a "mãozinha" pra indicar que é clicável
                >
                    <option value="" disabled>Selecione</option> {/* Opção vazia inicial */}
                    
                    {/* Cria as opções de 1 a 100 dinamicamente */}
                    {Array.from({ length: 100 }, (_, i) => i + 1).map((numero) => (
                    <option key={numero} value={numero}>
                        {numero}
                    </option>
                    ))}
                </select>
                </div>
                <div className="input-group" style={{ flex: 'none' }}>
                  <label>CEP:</label>
                  <input type="text" name="cep" value={dadosUsuario.cep} onChange={handleChange} className="cadastro-input input-small" />
                </div>
              </div>

              <div className="input-group">
                <label>Email:</label>
                <input type="email" name="email" value={dadosUsuario.email} onChange={handleChange} className="cadastro-input" />
              </div>

              <div className="input-group">
                <label>Confirmar Email:</label>
                <input type="email" name="confirmarEmail" value={dadosUsuario.confirmarEmail} onChange={handleChange} className="cadastro-input" />
              </div>

              <div className="input-row">
                <div className="input-group">
                  <label>Senha:</label>
                  <input type="password" name="senha" value={dadosUsuario.senha} onChange={handleChange} className="cadastro-input" />
                </div>
                <div className="input-group">
                  <label>Confirmar Senha:</label>
                  <input type="password" name="confirmarSenha" value={dadosUsuario.confirmarSenha} onChange={handleChange} className="cadastro-input" />
                </div>
              </div>

              <div className="form-actions">
                <button className="btn-cadastro btn-amarelo" style={{ padding: '10px 30px' }} onClick={voltar}>Voltar</button>
                <button className="btn-cadastro btn-rosa" style={{ padding: '10px 30px' }} onClick={avancarFormulario}>Avançar</button>
              </div>

            </div>
          </>
        )}

        {/* ========================================================= */}
        {/* PASSO 2: INSTITUIÇÃO */}
        {/* Só aparece se estiver no passo 2 E for instituição */}
        {/* ========================================================= */}
        {passoAtual === 2 && dadosUsuario.tipoUsuario === 'instituicao' && (
          <>
            <h1 className="cadastro-title" style={{ color: '#2C8E75' }}>Cadastro Instituição</h1>
            <div className="form-container">
              
              <div className="input-group">
                <label>Nome:</label>
                <input type="text" name="nome" value={dadosUsuario.nome} onChange={handleChange} className="cadastro-input" />
              </div>

              {/* Nova linha com 3 itens dividindo o espaço proporcionalmente */}
              <div className="input-row" style={{ justifyContent: 'space-between' }}>
                <div className="input-group" style={{ width: '150px' }}>
                  <label>CNPJ:</label>
                  <input type="text" name="cnpj" value={dadosUsuario.cnpj} onChange={handleChange} className="cadastro-input" maxLength="14"/>
                </div>
                <div className="input-group" style={{ width: '90px' }}>
                  <label>CEP:</label>
                  <input type="text" name="cep" value={dadosUsuario.cep} onChange={handleChange} className="cadastro-input" maxLength="9" />
                </div>
                <div className="input-group" style={{ width: '30px' }}>
                  <label>Número:</label>
                  <input type="text" name="numero" value={dadosUsuario.numero} onChange={handleChange} className="cadastro-input" maxLength="6" />
                </div>
              </div>

              <div className="input-group">
                <label>Email:</label>
                <input type="email" name="email" value={dadosUsuario.email} onChange={handleChange} className="cadastro-input" />
              </div>

              <div className="input-group">
                <label>Confirmar Email:</label>
                <input type="email" name="confirmarEmail" value={dadosUsuario.confirmarEmail} onChange={handleChange} className="cadastro-input" />
              </div>

              <div className="input-row">
                <div className="input-group">
                  <label>Senha:</label>
                  <input type="password" name="senha" value={dadosUsuario.senha} onChange={handleChange} className="cadastro-input" />
                </div>
                <div className="input-group">
                  <label>Confirmar Senha:</label>
                  <input type="password" name="confirmarSenha" value={dadosUsuario.confirmarSenha} onChange={handleChange} className="cadastro-input" />
                </div>
              </div>

              <div className="form-actions">
                <button className="btn-cadastro btn-amarelo" style={{ padding: '10px 30px' }} onClick={voltar}>Voltar</button>
                <button className="btn-cadastro btn-rosa" style={{ padding: '10px 30px' }} onClick={avancarFormulario}>Confirmar</button>
              </div>

            </div>
          </>
        )}
        {/* ========================================================= */}
        {/* PASSO 3: O QUIZ ESPECÍFICO (Só para usuários comuns) */}
        {/* ========================================================= */}
        {passoAtual === 3 && dadosUsuario.tipoUsuario === 'comum' && (
          <>
            {/* Título Laranja igual ao design */}
            <h1 className="cadastro-title" style={{ color: 'var(--accent-laranja)', marginBottom: '30px' }}>
              Quais são seus interesses?
            </h1>

            {/* Container dos botões gerados pelo map */}
            <div style={{ 
              display: 'flex', 
              gap: '15px', 
              justifyContent: 'center', 
              flexWrap: 'wrap', 
              maxWidth: '800px', 
              marginBottom: '40px' 
            }}>
              
              {/* O map lendo a nossa lista lá de cima */}
              {opcoesInteressesEspecificos.map((opcao) => (
                <button
                  key={opcao}
                  onClick={() => toggleInteresse(opcao)} // Reutilizamos a mesma função mágica!
                  style={{
                    padding: '12px 25px',
                    borderRadius: '8px',
                    border: '1px solid var(--bordas)',
                    backgroundColor: dadosUsuario.interesses.includes(opcao) ? '#F6D056' : 'transparent',
                    color: 'var(--bordas)',
                    fontSize: '1.1rem',
                    cursor: 'pointer',
                    minWidth: '140px',
                    transition: 'background-color 0.2s'
                  }}
                >
                  {opcao}
                </button>
              ))}

            </div>

            {/* Botões de Ação (Com o botão Salvar que leva pro Feed) */}
            <div className="form-actions" style={{ width: '100%', maxWidth: '500px', justifyContent: 'space-between' }}>
              <button className="btn-cadastro btn-amarelo" style={{ padding: '10px 30px' }} onClick={voltar}>
                Voltar
              </button>
              <button className="btn-cadastro btn-rosa" style={{ padding: '10px 30px' }} onClick={finalizarCadastro}>
                Salvar
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default Cadastro;