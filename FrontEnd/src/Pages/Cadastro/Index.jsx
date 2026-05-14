import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../index.css'; 
import './Cadastro.css'; 
import { NavbarPrincipal } from '../../assets/NavBar/navbar.jsx'; 

export function Cadastro() {
  const navigate = useNavigate();
  const [passoAtual, setPassoAtual] = useState(1);

  const [dadosUsuario, setDadosUsuario] = useState({
    tipoUsuario: '', 
    nome: '', 
    idade: '', 
    cep: '',
    email: '', 
    confirmarEmail: '',
    senha: '', 
    confirmarSenha: '', 
    cnpj: '', 
    numero: '', 
    interesses: []
  });

  const handleChange = (e) => {
    let { name, value } = e.target; // Usamos "let" em vez de "const" porque vamos modificar o valor

    // MÁSCARA DO CNPJ: Formata automaticamente enquanto digita
    if (name === 'cnpj') {
      value = value.replace(/\D/g, ''); // Remove tudo o que não for número (impede letras)
      value = value.replace(/^(\d{2})(\d)/, '$1.$2'); // Coloca o primeiro ponto
      value = value.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3'); // Coloca o segundo ponto
      value = value.replace(/\.(\d{3})(\d)/, '.$1/$2'); // Coloca a barra
      value = value.replace(/(\d{4})(\d{1,2})$/, '$1-$2'); // Coloca o traço
    }

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

  // ARRUMADO: Lógica simplificada para avançar
  const avancarFormulario = () => {
    // 1. VALIDAÇÃO GERAL: Emails e Senhas precisam bater
    if (dadosUsuario.email !== dadosUsuario.confirmarEmail) {
      alert("Os e-mails não coincidem! Verifique e tente novamente.");
      return; // O "return" expulsa a pessoa da função e não deixa o código continuar
    }

    if (dadosUsuario.senha !== dadosUsuario.confirmarSenha) {
      alert("As senhas não coincidem!");
      return;
    }

    // 2. VALIDAÇÃO INSTITUIÇÃO
    if (dadosUsuario.tipoUsuario === 'instituicao') {
      
      // Checa se ALGUM dos campos importantes está vazio
      if (!dadosUsuario.nome || !dadosUsuario.cnpj || !dadosUsuario.cep || !dadosUsuario.numero || !dadosUsuario.email || !dadosUsuario.senha) {
        alert("Atenção! Todos os dados da Instituição são obrigatórios.");
        return;
      }
      
      setPassoAtual(5); // Se passou por todos os "ifs", vai para a tela de aviso!

    // 3. VALIDAÇÃO USUÁRIO COMUM
    } else {
      
      // Checa se os campos essenciais do usuário estão vazios
      if (!dadosUsuario.nome || !dadosUsuario.email || !dadosUsuario.senha) {
        alert("Atenção! Preencha seu Nome, Email e Senha para continuar.");
        return;
      }

      setPassoAtual(passoAtual + 1); // Se passou por todos os "ifs", vai para o Quiz!
    }
  };

  const toggleInteresse = (interesseClicado) => {
    setDadosUsuario((dadosAntigos) => {
      const listaInteresses = dadosAntigos.interesses;
      if (listaInteresses.includes(interesseClicado)) {
        return { ...dadosAntigos, interesses: listaInteresses.filter(i => i !== interesseClicado) };
      } else {
        return { ...dadosAntigos, interesses: [...listaInteresses, interesseClicado] };
      }
    });
  };

  const [opcoesInteressesEspecificos, setOpcoesInteressesEspecificos] = useState([]);

  useEffect(() => {
    const buscarCategorias = async () => {
      try {
        const resposta = await fetch(import.meta.env.VITE_API_URL + '/categorias');
        const dados = await resposta.json();
        setOpcoesInteressesEspecificos(dados);
      } catch (erro) {
        console.error("Erro ao carregar categorias:", erro);
      }
    };
    buscarCategorias();
  }, []);

  const finalizarCadastro = async () => {
    console.log("Dados preparados para envio:", dadosUsuario);

    // 1. A MÁGICA: Salvamos todos os dados no navegador para a tela de Perfil conseguir ler!
    localStorage.setItem('usuarioWisp', JSON.stringify(dadosUsuario));

    // 2. Tentamos enviar para o Java (Backend)
    try {
      const resposta = await fetch('http://localhost:8080/api/usuarios/cadastrar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(dadosUsuario)
      });
      
      if (resposta.ok) {
        const idDoUsuario = await resposta.text();
        localStorage.setItem('wisp_userId', idDoUsuario);
      }
    } catch (erro) {
      // Se o Java estiver desligado, o código cai aqui, avisa no console, mas não quebra a tela!
      console.warn("Aviso: Backend Java não encontrado. Os dados foram salvos localmente para teste visual.", erro);
    }

    // 3. Mandamos o usuário direto para a tela de perfil para ele ver os dados dele! 
    // (Se preferir, pode voltar para '/feed')
    navigate('/perfiluc');
    console.log("Dados enviados:", dadosUsuario);
    const resposta = await fetch(import.meta.env.VITE_API_URL + '/usuarios/cadastrar', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(dadosUsuario)
    });
    const idDoUsuario = await resposta.text();
    localStorage.setItem('wisp_userId', idDoUsuario);
    navigate('/feed');
  };

  return (
    <div className="cadastro-page">
      <NavbarPrincipal mostrarBusca={false} mostrarHamburger={false} />

      <div className="main-container cadastro-content">

        {/* PASSO 1 */}
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

        {/* PASSO 2: USUÁRIO COMUM */}
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
                  <select name="idade" value={dadosUsuario.idade} onChange={handleChange} className="cadastro-input input-small">
                    <option value="" disabled>Selecione</option>
                    {Array.from({ length: 100 }, (_, i) => i + 1).map((num) => (
                      <option key={num} value={num}>{num}</option>
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
                <button className="btn-cadastro btn-amarelo" onClick={voltar}>Voltar</button>
                <button className="btn-cadastro btn-rosa" onClick={avancarFormulario}>Avançar</button>
              </div>
            </div>
          </>
        )}

        {/* PASSO 2: INSTITUIÇÃO */}
        {passoAtual === 2 && dadosUsuario.tipoUsuario === 'instituicao' && (
          <>
            <h1 className="cadastro-title" style={{ color: '#2C8E75' }}>Cadastro Instituição</h1>
            <div className="form-container">
              <div className="input-group">
                <label>Nome:</label>
                <input type="text" name="nome" value={dadosUsuario.nome} onChange={handleChange} className="cadastro-input" />
              </div>
              <div className="input-row" style={{ justifyContent: 'space-between' }}>
                <div className="input-group" style={{ width: '150px' }}>
                  <label>CNPJ:</label>
                  <input type="text" name="cnpj" value={dadosUsuario.cnpj} onChange={handleChange} className="cadastro-input" maxLength="18"/>
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
                <button className="btn-cadastro btn-amarelo" onClick={voltar}>Voltar</button>
                <button className="btn-cadastro btn-rosa" onClick={avancarFormulario}>Confirmar</button>
              </div>
            </div>
          </>
        )}

        {/* PASSO 3: QUIZ */}
        {passoAtual === 3 && dadosUsuario.tipoUsuario === 'comum' && (
          <>
            <h1 className="cadastro-title" style={{ color: 'var(--accent-laranja)', marginBottom: '30px' }}>Quais são seus interesses?</h1>
            <div style={{ display: 'flex', gap: '15px', justifyContent: 'center', flexWrap: 'wrap', maxWidth: '800px', marginBottom: '40px' }}>
              {opcoesInteressesEspecificos.map((opcao) => (
                <button
                  key={opcao}
                  onClick={() => toggleInteresse(opcao)}
                  style={{
                    padding: '12px 25px', borderRadius: '8px', border: '1px solid var(--bordas)',
                    backgroundColor: dadosUsuario.interesses.includes(opcao) ? '#F6D056' : 'transparent',
                    color: 'var(--bordas)', fontSize: '1.1rem', cursor: 'pointer', minWidth: '140px'
                  }}
                >
                  {opcao}
                </button>
              ))}
            </div>
            <div className="form-actions" style={{ width: '100%', maxWidth: '500px' }}>
              <button className="btn-cadastro btn-amarelo" onClick={voltar}>Voltar</button>
              <button className="btn-cadastro btn-rosa" onClick={finalizarCadastro}>Salvar</button>
            </div>
          </>
        )}

        {/* PASSO 5: AVISO ANALISE (Movido para dentro do return) */}
        {passoAtual === 5 && dadosUsuario.tipoUsuario === 'instituicao' && (
          <div style={{ textAlign: 'center', maxWidth: '500px', marginTop: '40px' }}>
            <h1 className="cadastro-title" style={{ color: 'var(--accent-laranja)', marginBottom: '20px' }}>Cadastro em Análise! ⏳</h1>
            <p style={{ fontSize: '1.2rem', color: 'var(--bordas)', marginBottom: '40px', lineHeight: '1.5', fontWeight: '500' }}>
              Seu cadastro foi enviado para a análise e deverá ser liberado em até 7 dias.
            </p>
            <button className="btn-cadastro btn-comum" style={{ padding: '12px 40px' }} onClick={() => navigate('/')}>
              Voltar para o Início
            </button>
          </div>
        )}

      </div>
    </div>
  );
}

export default Cadastro;