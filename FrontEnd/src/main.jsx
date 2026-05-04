import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import Feed from './Pages/Feed/Index.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Feed />
  </StrictMode>,
)
